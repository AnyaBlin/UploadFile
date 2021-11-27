package filestests;

import com.codeborne.pdftest.PDF;
import com.codeborne.selenide.Condition;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestsWithFiles {

    String URL = "https://demoqa.com/upload-download";
    String fileNAME = "example.txt";

    @Test
    @DisplayName("Загрузка файла по относительному пути")
    public void uploadFiles() {
        open(URL);
        $("input[type = 'file']").uploadFromClasspath(fileNAME);
        $("#uploadedFilePath").shouldHave(Condition.text(fileNAME));
    }

    String downloadURL = "https://github.com/MadeBaruna/paimon-moe/blob/main/LICENSE";

    @Test
    @DisplayName("Скачивание файла и проверка его содержимого")
    public void downloadFiles() throws IOException {
        open(downloadURL);
        File download = $("#raw-url").download();
        String fileContent = IOUtils.toString(new FileReader(download));
        assertTrue(fileContent.contains("Copyright (c) 2021 Made Baruna"));
    }

    String downloadPDFURL = "https://litportal.ru/avtory/kristofer-hart/kniga-rukovodstvo-po-risovaniyu-anime-1067993.html";

    @Test
    @DisplayName("Скачивание PDF файла")
    public void pdfFileDownload() throws IOException {
        open(downloadPDFURL);
        File pdf = $(By.linkText("Скачать pdf")).download();
        PDF parsedPdf = new PDF(pdf);
        Assertions.assertEquals(14, parsedPdf.numberOfPages);
    }

    String downloadXLSURL = "https://xn----9sbjebdr5a7bi8ipa.xn--p1ai/kalorijnost-produktov-tablicza-excel";

    @Test
    @DisplayName("Скачивание XLS файла")
    public void xlsFileDownload() throws IOException {
        open(downloadXLSURL);
        File xls = $(By.linkText("Скачать таблицу калорийности продуктов в Excel")).download();
        XLS parsedXls = new XLS(xls);
        parsedXls.excel.getSheetAt(0).getRow(5).getCell(0).getStringCellValue().contains("Абрикосы");
    }

    @Test
    @DisplayName("Парсинг CSV")
    void parseCSVFile() throws IOException, CsvException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("csv.csv");
             Reader reader = new InputStreamReader(is)) {
            CSVReader csvReader = new CSVReader(reader);

            List<String[]> strings = csvReader.readAll();
            assertEquals(1, strings.size());
        }
    }

    @Test
    @DisplayName("Парсинг ZIP файлов")
    void parseZIPFile() throws IOException, CsvException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("csv.csv");
             ZipInputStream zip = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                System.out.println(entry.getName());
            }
        }
    }
}



