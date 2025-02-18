package src;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import src.Konfrontationstyp;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import static java.lang.Character.isLowerCase;
import static java.lang.Character.toUpperCase;

public class Main {
    public class Evenimente {
        Integer id;
        String held;
        String antagonist;
        Konfrontationstyp konfrontationstyp;
        String ort;
        LocalDate datum;
        Double globalerEinfluss;

        @Override
        public String toString() {
            return held;
        }
    }

    public List<Evenimente> readData(String filename) throws ParserConfigurationException, IOException, SAXException {
        List<Evenimente> events = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(filename));

        NodeList logList = doc.getElementsByTagName("log");
        for (int i = 0; i < logList.getLength(); i++) {
            Evenimente event = new Evenimente();
            event.id = Integer.parseInt(doc.getElementsByTagName("Id").item(i).getTextContent());
            event.held = doc.getElementsByTagName("Held").item(i).getTextContent();
            event.antagonist = doc.getElementsByTagName("Antagonist").item(i).getTextContent();
            event.konfrontationstyp = Konfrontationstyp.valueOf(doc.getElementsByTagName("Konfrontationstyp").item(i).getTextContent());
            event.ort = doc.getElementsByTagName("Ort").item(i).getTextContent();
            event.datum = LocalDate.parse(doc.getElementsByTagName("Datum").item(i).getTextContent());
            event.globalerEinfluss = Double.valueOf(doc.getElementsByTagName("GlobalerEinfluss").item(i).getTextContent());
            events.add(event);
        }
        return events;
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        List<Main.Evenimente> events = main.readData("src/evenimente.xml");

        System.out.println("Enter a character: ");
        Scanner scanner = new Scanner(System.in);
        int input = scanner.nextInt();

        events.stream().filter(e ->e.globalerEinfluss >= Double.parseDouble(String.valueOf(input)))
                .distinct()
                .forEach(System.out::println);

        events.stream()
                .filter(e->e.konfrontationstyp.equals(Konfrontationstyp.Galaktisch))
                .sorted(Comparator.comparing(akt-> {
                    try {
                        return new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(akt.datum));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .forEach(e->System.out.printf("%s: %s vs. %s - %s%n", e.datum, e.held, e.antagonist, e.ort));
    }


}
