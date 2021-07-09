package dom;

import dom.model.Levels;
import dom.model.ModuleTraining;
import dom.model.Training;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DomParser {

    public static void main(String[] args) throws Exception {

        Training training = new Training();

        DomParser domParser = new DomParser();
        Document document = domParser.buildDocument();

        Node trainingNode = document.getFirstChild();

        NodeList trainingChilds = trainingNode.getChildNodes();

        String mainName = null;
        Node modulesNode = null;
        Levels level = null;
        for (int i = 0; i < trainingChilds.getLength(); i++) {
            if (trainingChilds.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (trainingChilds.item(i).getNodeName()) {
                case "name": {
                    mainName = trainingChilds.item(i).getTextContent();
                    break;
                }
                case "modules": {
                    modulesNode = trainingChilds.item(i);
                    break;
                }
                case "levels": {
                    String levelValue = trainingChilds.item(i).getFirstChild().getTextContent();
                    level = Levels.valueOf(levelValue);
                }
            }
        }

        if (modulesNode == null) {
            return;
        }
        List<ModuleTraining> moduleTrainingList = parseModuleList(modulesNode);

        training.setModuleTrainingList(moduleTrainingList);
        training.setName(mainName);
        training.setLevels(level);
        System.out.println(training);
    }


    private Document buildDocument() throws Exception {
        URL url = this.getClass().getClassLoader().getResource("trainingDOM.xml");

        File file = new File(url.getPath());
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        return factory.newDocumentBuilder().parse(file);
    }


    private static List<ModuleTraining> parseModuleList(Node modulesNode) {
        List<ModuleTraining> moduleTrainingList = new ArrayList<>();
        NodeList moduleChilds = modulesNode.getChildNodes();
        for (int i = 0; i < moduleChilds.getLength(); i++) {
            if (moduleChilds.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (!moduleChilds.item(i).getNodeName().equals("element")) {
                continue;
            }
            int id = 0;
            String title = null;
            String description = null;
            LocalDate publishDate = null;
            NodeList elementChilds = moduleChilds.item(i).getChildNodes();
            for (int j = 0; j < elementChilds.getLength(); j++) {
                if (elementChilds.item(j).getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                switch (elementChilds.item(j).getNodeName()) {
                    case "id": {
                        id = Integer.parseInt(elementChilds.item(j).getTextContent());
                        break;
                    }
                    case "title": {
                        title = elementChilds.item(j).getTextContent();
                        break;
                    }
                    case "description": {
                        description = elementChilds.item(j).getTextContent();
                        break;
                    }
                    case "publishDate": {
                        publishDate = LocalDate.parse(elementChilds.item(j).getTextContent());
                        break;
                    }
                }
            }
            ModuleTraining moduleTraining = new ModuleTraining(id, title, description, publishDate);
            moduleTrainingList.add(moduleTraining);
        }
        return moduleTrainingList;
    }
}
