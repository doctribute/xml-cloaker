/*
 * Copyright 2016-present doctribute (http://doctribute.com/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * Jan Tosovsky
 */
package org.doctribute.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Java class for escaping DTD, entities, XIncludes and other XML content
 * which need to be preserved, in certain scenarios, during XSLT transformation.
 * <p>
 * When XML document is parsed, several irreversible changes are applied, namely
 * <ul>
 * <li>all referenced entities are replaced with their values</li>
 * <li>all referenced content (XML includes) is merged</li>
 * <li>all default DTD attribute values are injected</li>
 * </ul>
 * <p>
 * If the purpose of particular XSLT transformation is to modify the source XML,
 * e.g. alter the structure, update attribute values or insert comments, it is
 * not desired to touch the rest of the content.
 * <p>
 * In these scenarios it is handy to cloak the XML first, perform the
 * transformation and finally uncloak the content back.
 *
 * @version 1.0.0
 */
public final class Cloaker {

    private static final String COMMENT = "DO NOT REMOVE THIS COMMENT! This is a 'cloaked' document";
    private static final String REGEX_DTD_INTERNAL = "<!(DOCTYPE[^>]*\\[[^]]*\\]\\s*)>";
    private static final String REGEX_DTD_BASIC = "<!(DOCTYPE[^>]*)>";
    private static final String REGEX_CDATA = "<!\\[CDATA\\[(.*?)\\]\\]>";
    private static final Pattern PATTERN_DTD_INTERNAL = Pattern.compile(REGEX_DTD_INTERNAL);
    private static final Pattern PATTERN_DTD_BASIC = Pattern.compile(REGEX_DTD_BASIC);
    private static final Pattern PATTERN_CDATA = Pattern.compile(REGEX_CDATA);

    /**
     * Returns the cloaked content of the specified XML file in the form of
     * InputStream.
     *
     * @param sourceXmlPath The source XML path.
     * @return The cloaked content in the form of InputStream.
     * @throws IOException
     * @since 1.0.0
     */
    public static InputStream getCloakedInputStream(Path sourceXmlPath) throws IOException {

        String sourceXmlContent = new String(Files.readAllBytes(sourceXmlPath), StandardCharsets.UTF_8);
        String cloakedXmlContent = getCloakedContent(sourceXmlContent);

        return new ByteArrayInputStream(cloakedXmlContent.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Returns the cloaked variant of the specified XML content.
     *
     * @param sourceXmlContent The source XML content.
     * @return The cloaked content.
     * @since 1.0.0
     */
    public static String getCloakedContent(String sourceXmlContent) {

        String content = sourceXmlContent;

        content = content + "<?" + COMMENT + "?>";
        content = content.replace("xi:include", "xnclude");

        Matcher matcher = PATTERN_DTD_INTERNAL.matcher(content);

        if (matcher.find()) {

            String dtd = matcher.group(1);

            dtd = dtd.replace("[", "xxLEFT_SQUARE_BRACKETxx");
            dtd = dtd.replace("]", "xxRIGHT_SQUARE_BRACKETxx");
            dtd = dtd.replace(">", "xxGREATER_THANxx");
            dtd = dtd.replace("<", "xxLESS_THANxx");

            content = content.replace(matcher.group(0), "<?" + dtd + "_END_SUBSET?>");

        } else {

            matcher = PATTERN_DTD_BASIC.matcher(content);

            if (matcher.find()) {
                content = content.replace(matcher.group(0), "<?" + matcher.group(1) + "_END_SUBSET?>");
            }
        }

        matcher = PATTERN_CDATA.matcher(content);

        while (matcher.find()) {

            String cdata = matcher.group(1);
            cdata = cdata.replace("&", "##AMP_CDATA_ENT##");
            content = content.replace(matcher.group(1), cdata);
        }

        // cloak the rest of entities
        content = content.replace("&", "##AMP_ENT##");

        return content;
    }

    /**
     * Returns the uncloaked variant of the specified cloaked XML content.
     *
     * @param cloakedXmlContent The cloaked XML content.
     * @return The uncloaked content.
     * @since 1.0.0
     */
    public static String getUncloakedContent(String cloakedXmlContent) {

        String content = cloakedXmlContent;

        content = content.replace("<?" + COMMENT + "?>", "\n");
        content = content.replace("##AMP_ENT##", "&");
        content = content.replace("##AMP_CDATA_ENT##", "&amp;");
        content = content.replace("xnclude", "xi:include");
        content = content.replace("xxLEFT_SQUARE_BRACKETxx", "[");
        content = content.replace("xxRIGHT_SQUARE_BRACKETxx", "]");
        content = content.replace("xxGREATER_THANxx", ">");
        content = content.replace("xxLESS_THANxx", "<");
        content = content.replace("_END_SUBSET?>", ">\n");
        content = content.replace("<?DOCTYPE", "\n<!DOCTYPE");

        return content;
    }

    /**
     * Returns true if the specified source XML content is cloaked.
     *
     * @param sourceXmlContent The source XML content.
     * @return A boolean value true if the specified source XML content is
     * cloaked.
     * @since 1.0.0
     */
    public static boolean isContentCloaked(String sourceXmlContent) {
        return sourceXmlContent.contains(COMMENT);
    }
}
