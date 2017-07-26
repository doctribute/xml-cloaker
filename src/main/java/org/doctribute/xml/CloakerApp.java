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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * A command line cloaker.
 * <p>
 * For usage just run the tool in console without any parameters:
 * <p>
 * <code>
 * java -jar xml-cloaker-{version}.jar
 * </code>
 *
 * @version 1.0.0
 */
public class CloakerApp {

    private static final String PARAM_SRC = "-src";
    private static final String PARAM_DEST = "-dest";

    public static void main(String[] args) throws IOException {

        Map<String, String> passedValuesMap = new HashMap<>();

        for (String arg : args) {
            int index = arg.indexOf(":");
            if (index > 0 && index < arg.length() - 1) {
                passedValuesMap.put(arg.substring(0, index), arg.substring(index + 1));
            }
        }

        if (passedValuesMap.containsKey(PARAM_SRC) && passedValuesMap.containsKey(PARAM_DEST)) {

            Path sourcePath = Paths.get(passedValuesMap.get(PARAM_SRC));
            Path destinationPath = Paths.get(passedValuesMap.get(PARAM_DEST));

            if (!Files.exists(sourcePath)) {
                throw new IOException("The specified path was not found: " + sourcePath.toString());
            }

            String sourceContent = new String(Files.readAllBytes(sourcePath), StandardCharsets.UTF_8);
            String destinationContent = Cloaker.isContentCloaked(sourceContent) ? Cloaker.getUncloakedContent(sourceContent) : Cloaker.getCloakedContent(sourceContent);

            try (OutputStream outputStream = Files.newOutputStream(destinationPath)) {
                outputStream.write(destinationContent.getBytes(StandardCharsets.UTF_8));
            }

        } else {
            System.out.println("Usage:");
            System.out.println("java -jar xml-cloaker.jar ");
            System.out.println("     -src:source.xml");
            System.out.println("     -dest:destination.xml");
        }
    }
}
