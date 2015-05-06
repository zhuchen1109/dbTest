/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.greenrobot.daogenerator.gentest;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * Generates entities and DAOs for the example project DaoExample.
 * 
 * Run it as a Java application (not Android).
 * 
 * @author Markus
 */
public class ExampleDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(3, "com.example.dbtest.greendao.dao");

        addUser(schema);

        //生成的文件放到对于的目录。这是一个相对路径。要求dbtext项目和这个 ExampleDaoGenerator项目处于相同的目录下
        new DaoGenerator().generateAll(schema, "../dbTest/src-gen");
    }

    private static void addUser(Schema schema) {
        Entity note = schema.addEntity("users");
        note.addIdProperty();
        note.addStringProperty("name").notNull();
        note.addStringProperty("phone");
    }

}
