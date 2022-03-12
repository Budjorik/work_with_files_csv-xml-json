import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        // Задача № 1 (CSV - JSON парсер)
        // Создаем массив строчек, содержащий информацию о предназначении колонок в CSV файле
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        // Определяем имя для считываемого CSV файла
        String fileName = "data.csv";

        // Получаем список сотрудников, вызвав метод parseCSV()
        List<Employee> listOne = parseCSV(columnMapping, fileName);

        // Полученный список преобразуем в строчку в формате JSON
        String jsonOne = listToJson(listOne);

        // Запишем полученную строчку в формате JSON в файл
        writeString(jsonOne);

        // Задача №2 (XML - JSON парсер)
        // Получаем список сотрудников из XML документа
        List<Employee> listTwo = parseXML("data.xml");

        // Полученный список преобразуем в строчку в формате JSON
        String jsonTwo = listToJson(listTwo);

        // Запишем полученную строчку в формате JSON в файл
        writeString(jsonTwo);

        // Задача №3 (JSON парсер)
        // Получаем строку из файла JSON
        String jsonThree = readString("new_data.json");

        // Прочитанный JSON преобразовываем в список сотрудников
        List<Employee> listThree = jsonToList(jsonThree);

        // Выведим содержимое полученного списка в консоль
        showToList(listThree);

    }

    // Метод получения списка сотрудников (задача №1)
    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping("id", "firstName", "lastName", "coutry", "age");
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> list = csv.parse();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Метод преобразования списка в строчку в формате JSON (задача №1)
    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    // Метод записи полученной строчки в формате JSON в файл (задача №1)
    public static void writeString(String json) {
        try (FileWriter file = new FileWriter("new_data.json")) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод получения списка сотрудников из XML документа (задача №2)
    public static List<Employee> parseXML(String fileXml) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> listTwo = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileXml));
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0 ; i < nodeList.getLength() ; i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                Element element = (Element) node_;
                long id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                String country = element.getElementsByTagName("country").item(0).getTextContent();
                int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                listTwo.add(new Employee(id, firstName, lastName, country, age));
            }
        }
        return listTwo;
    }

    // Метод получения строки из файла JSON (задача №3)
    public static String readString(String fileJson) {
        String line = null;
        try (BufferedReader br = new BufferedReader(new FileReader(fileJson))) {
            String s;
            while ((s = br.readLine()) != null) {
                StringBuilder value = new StringBuilder();
                value.append(s);
                line = value.toString();
            }
            return line;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    // Метод преобразования строки JSON в список сотрудников (задача №3)
    public static List<Employee> jsonToList(String json) {
        JsonElement jsonElement = new JsonParser().parse(json);
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        List<Employee> list = new ArrayList<>();
        for (int i = 0 ; i < jsonArray.size() ; i++) {
            Employee employee = gson.fromJson(jsonArray.get(i), Employee.class);
            list.add(employee);
        }
        return list;
    }

    // Метод вывода на печать списка сотрудников (задача №3)
    public static void showToList(List<Employee> list) {
        for (Employee item : list) {
            System.out.println(item.toString());
        }
    }

}