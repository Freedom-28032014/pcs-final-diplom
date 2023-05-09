import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class BooleanSearchEngine implements SearchEngine {

    private Map<String, List<PageEntry>> listMap = new HashMap<>();
    private List<PageEntry> pageEntryResult = new ArrayList<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        // прочтите тут все pdf и сохраните нужные данные,
        // тк во время поиска сервер не должен уже читать файлы
//        List<PageEntry> pageEntryResult = new ArrayList<>(listMap.get(word.toLowerCase()));
//        pageEntryResult.sort(Collections.reverseOrder()); //метод sort(), который сортирует массив в порядке возрастания.
        for (File file : pdfsDir.listFiles()) { //Проходим циклом for each
            // (метод, позволяющий прочитать список только определенных файлов)
            //Создаем экземпляр PdfDocument
            PdfDocument doc = new PdfDocument(new PdfReader(file));
            //Загрузите файл PDF
            // не забываем, что нумерация страниц в PDF начинается с единицы.
            for (int i = 1; i <= doc.getNumberOfPages(); ++i) {
                var text = PdfTextExtractor.getTextFromPage(doc.getPage(i)); // получаем текст с PDF
                String pdfName = file.getName();//название файла
                int page = i;//станицы
                var words = text.split("\\P{IsAlphabetic}+");
                Map<String, Integer> freqs = new HashMap<>(); // мапа, где ключом будет слово, а значением - частота
                for (var word : words) { // перебираем слова
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                }

                for (String word : freqs.keySet()) { //проходим циклом for each по списку слов из мапы freqs
                    PageEntry pageEntry = new PageEntry(file.getName(), page, freqs.get(word));
                    if (!listMap.containsKey(word)) {
                        List<PageEntry> pageEntryList = new ArrayList<>();
                        pageEntryList.add(pageEntry);
                        listMap.put(word, pageEntryList);
                    } else {
                        listMap.get(word).add(pageEntry);
                    }


                    pageEntryResult = new ArrayList<>(listMap.get(word.toLowerCase()));
                    pageEntryResult.sort(Collections.reverseOrder()); //метод sort(), который сортирует массив в порядке возрастания.
                }
            }
        }

    }

    @Override
    public List<PageEntry> search(String word) {
        // тут реализуйте поиск по слову
        if ((word == null) || (word.isEmpty())) { // проверка на отсутствие искомого слова
            return Collections.emptyList();
        }


        return pageEntryResult;
    }
}