/* 
 * Copyright (c) 2016-present Jan Tošovský <jan.tosovsky.cz@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.doctribute.xml;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cloaker {

    private static final String COMMENT = "DO NOT REMOVE THIS COMMENT! This is a 'cloaked' document";
    private static final String REGEX_DTD_INTERNAL = "<!(DOCTYPE[^>]*\\[[^]]*\\]\\s*)>";
    private static final String REGEX_DTD_BASIC = "<!(DOCTYPE[^>]*)>";
    private static final String REGEX_CDATA = "<!\\[CDATA\\[(.*?)\\]\\]>";
    private static final Pattern PATTERN_DTD_INTERNAL = Pattern.compile(REGEX_DTD_INTERNAL);
    private static final Pattern PATTERN_DTD_BASIC = Pattern.compile(REGEX_DTD_BASIC);
    private static final Pattern PATTERN_CDATA = Pattern.compile(REGEX_CDATA);

    public static boolean isContentCloaked(String content) {
        return content.contains(COMMENT);
    }

    public static String getCloakedContent(String content) {

        String cloakedContent = content;

        cloakedContent = cloakedContent + "<?" + COMMENT + "?>";
        cloakedContent = cloakedContent.replace("xi:include", "xnclude");

        Matcher matcher = PATTERN_DTD_INTERNAL.matcher(cloakedContent);

        if (matcher.find()) {

            String dtd = matcher.group(1);

            dtd = dtd.replace("[", "xxLEFT_SQUARE_BRACKETxx");
            dtd = dtd.replace("]", "xxRIGHT_SQUARE_BRACKETxx");
            dtd = dtd.replace(">", "xxGREATER_THANxx");
            dtd = dtd.replace("<", "xxLESS_THANxx");

            cloakedContent = cloakedContent.replace(matcher.group(0), "<?" + dtd + "_END_SUBSET?>");

        } else {

            matcher = PATTERN_DTD_BASIC.matcher(cloakedContent);

            if (matcher.find()) {
                cloakedContent = cloakedContent.replace(matcher.group(0), "<?" + matcher.group(1) + "_END_SUBSET?>");
            }
        }

        matcher = PATTERN_CDATA.matcher(cloakedContent);

        while (matcher.find()) {

            String cdata = matcher.group(1);
            cdata = cdata.replace("&", "##AMP_CDATA_ENT##");
            cloakedContent = cloakedContent.replace(matcher.group(1), cdata);
        }

        cloakedContent = cloakedContent.replace("&", "##AMP_ENT##");

        return cloakedContent;
    }

    public static String getUncloakedContent(String content) {

        Map<String, String> replacementMap = new LinkedHashMap<>();

        replacementMap.put("<?" + COMMENT + "?>", "\n");
        replacementMap.put("##AMP_ENT##", "&");
        replacementMap.put("##AMP_CDATA_ENT##", "&amp;");
        replacementMap.put("xnclude", "xi:include");
        replacementMap.put("xxLEFT_SQUARE_BRACKETxx", "[");
        replacementMap.put("xxRIGHT_SQUARE_BRACKETxx", "]");
        replacementMap.put("xxGREATER_THANxx", ">");
        replacementMap.put("xxLESS_THANxx", "<");
        replacementMap.put("_END_SUBSET?>", ">\n");
        replacementMap.put("<?DOCTYPE", "\n<!DOCTYPE");

        return getReplacedContent(content, replacementMap);
    }

    private static String getReplacedContent(String content, Map<String, String> replacementMap) {

        String replacedContent = content;

        for (Entry<String, String> entry : replacementMap.entrySet()) {
            replacedContent = replacedContent.replace(entry.getKey(), entry.getValue());
        }

        return replacedContent;
    }
}
