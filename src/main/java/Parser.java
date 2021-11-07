import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class Parser {
    private static final Logger LOGGER = Logger.getLogger(Parser.class);
    public static void main(String[] args) {
        System.out.println("Enter URL:");
        Scanner scn = new Scanner(System.in);
        String url = scn.nextLine();
        try {
            String text = getPage(url).text();
            String separatorsString = "[\s\".,!?\\\\;:\\[\\]()\\n\\r\\t'`]+";
            Map<String,Word> count = new HashMap<>();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8))));
            String line;
            while ((line = bufferedReader.readLine()) != null){
                String[] words = line.split(separatorsString);
                for (int i = 0; i < words.length; i++) {
                    if (words[i].contains("\"")){
                        words[i] = words[i].replaceAll("\"","");
                    }
                    if (words[i].contains(",")){
                        words[i] = words[i].replaceAll(",","");
                    }
                }
                for (String word : words){
                    if ("".equals(word)){
                        continue;
                    }
                    Word wordObject = count.get(word);
                    if (wordObject == null){
                        wordObject = new Word();
                        wordObject.word = word;
                        wordObject.count = 0;
                        count.put(word,wordObject);
                    }
                    wordObject.count++;
                }
            }
            bufferedReader.close();
            SortedSet<Word> sortedWords = new TreeSet<>(count.values());
            List<Word> wordList = new ArrayList<>(sortedWords);
            for (Word word: wordList) {
                System.out.println("Word: " + word.word + " || " + "Quantity: " + word.count);
            }
            try {
                SaveToDB(wordList,url);
            }
            catch (Throwable cause){
                LOGGER.error("Cannot save to DataBase",cause);
            }
        }
        catch (Throwable cause){
            LOGGER.error("Wrong URL", cause);
        }

    }

    public static org.jsoup.nodes.Document getPage(String url) throws IOException {
        return Jsoup.parse(new URL(url), 7000);
    }
    public static void SaveToDB(List<Word> list,String url) throws SQLException {
        DBConnection DB = new DBConnection();
        DB.createTable();
        for (Word word : list){
            String wrd = word.word;
            int count = word.count;
            PreparedStatement preparedStatement = DB.connection.prepareStatement("INSERT INTO `statistics` (date,url,word,count) VALUE (?,?,?,?)");
            preparedStatement.setDate(1,new java.sql.Date(new java.util.Date().getTime()));
            preparedStatement.setString(2,url);
            preparedStatement.setString(3,wrd);
            preparedStatement.setInt(4,count);
            DB.updateDB(preparedStatement);
        }
    }
}
