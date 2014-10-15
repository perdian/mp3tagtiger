/*
 * Copyright 2014 Christian Robert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tagtiger;

import javafx.application.Application;
import javafx.stage.Stage;
import de.perdian.apps.tagtiger.business.framework.localization.Localization;

public class ComponentDevelopmentApplication extends Application {

    public static void main(String[] args) {
        Application.launch(ComponentDevelopmentApplication.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Localization localization = new Localization() {};

//        TreeView<File> tree = new TreeView<>(new DirectoryTreeItem(new File))
//
//        Scene scene = new Scene(selectionPane);
//
//        primaryStage.setScene(scene);
//        primaryStage.setOnCloseRequest(event -> System.exit(0));
//        primaryStage.setTitle("DEV");
//        primaryStage.setWidth(600);
//        primaryStage.setHeight(700);
//        primaryStage.show();
//
//        selectionPane.setSelectedDirectory(new File("C:/Temp/1/2/"));
//        selectionPane.setSelectedDirectory(new File("C:/Temp/1/2/3/4/5/"));

    }

}
