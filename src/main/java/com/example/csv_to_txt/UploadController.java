package com.example.csv_to_txt;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadController {

    @RequestMapping(value = "/uploadDoubleQudoteCSV")
    public String uploadDoubleQudoteCSV() {
        return "doubleQudoteCSV";
    }

    @RequestMapping(value = "/uploadSingleQudoteCSV")
    public String uploadSingleQudoteCSV() {
        return "singleQudoteCSV";
    }

    @RequestMapping(value = "/uploadDoubleQudoteCSV", method = { RequestMethod.POST, RequestMethod.GET })
    public void uploadDoubleQudoteCSV(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        File cacheFile = null;
        BufferedWriter writer1 = null;
        try {
            // Convert MultipartFile to File
            cacheFile = File.createTempFile(file.getOriginalFilename(), ".xlsx");
            file.transferTo(cacheFile);

            // header
            StringBuffer data = new StringBuffer();
            data.append("1").append(String.format("%-19s", "2263018755")).append("VA1")
            .append(new SimpleDateFormat("yyyyMMdd").format(new Date()))
            .append(new SimpleDateFormat("HHmmss").format(new Date())).append("MYR")
            .append(String.format("%-140s", "SHENMA CREDIT SDN BHD"))
            .append(new SimpleDateFormat("yyyyMMdd").format(new Date()))
            .append(String.format("%-812s", "")).append("\r\n");
            
            String line = "";  
            String splitBy = "\",\"";  
            BufferedReader br = new BufferedReader(new FileReader(cacheFile));
            Boolean startAtSecond = false;
            BigDecimal total = BigDecimal.valueOf(0);
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (startAtSecond) {
                    String[] info = line.split(splitBy);
                    String accountNumber = info[1].replaceAll("\"", "");
                    String date = info[3].replaceAll("\"", "");
                    String time = info[4].replaceAll("\"", "");
                    String description  = info[5].replaceAll("\"", "");
                    String yourReference  = info[6].replaceAll("\"", "").replaceAll(" ", "");
                    String deposit  = info[17].replaceAll("\"", "").replaceAll(" ", "");
                    
                    Date formatterDate = new SimpleDateFormat("dd/MM/yyyy").parse(date.replaceAll(" ", ""));
                    String yymmdd = new SimpleDateFormat("yyyyMMdd").format(formatterDate);
                    Date formatterTime = new SimpleDateFormat("hh:mm:ssaa").parse(time.replaceAll(" ", ""));
                    String hhmmss = new SimpleDateFormat("HHmmss").format(formatterTime);
                    count += 1;
                    
                    data.append("2").append(yymmdd).append(hhmmss)
                    .append(String.format("%-34s", new BigDecimal(yourReference).setScale(0).toString()))
                    .append(String.format("%-6s", "")).append(String.format("%-34s", "")).append(String.format("%-6s", ""))
                    .append("C").append("T").append("MYR");
                    
                    // amount
                    BigDecimal depositAmt = new BigDecimal(deposit).setScale(0, RoundingMode.FLOOR);
                    
                    // decimal
                    BigDecimal amount = new BigDecimal(deposit).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal decimalToInt = amount.subtract(amount.setScale(0, RoundingMode.FLOOR)).movePointRight(amount.scale());
                    total = total.add(amount);
                    
                    data.append(String.format("%015d", Integer.parseInt(depositAmt.toString())));
                    if (decimalToInt.compareTo(BigDecimal.ZERO) == 0) {
                        data.append(String.format("%-2s", "00"));
                    } else {
                        data.append(String.format("%-2s", decimalToInt.toString()));
                    }
                    data.append(String.format("%015d", Integer.parseInt(depositAmt.toString())));
                    if (decimalToInt.compareTo(BigDecimal.ZERO) == 0) {
                        data.append(String.format("%-2s", "00"));
                    } else {
                        data.append(String.format("%-2s", decimalToInt.toString()));
                    }
                    
                    // Client Account Name(can't get it in excel)
                    data.append(String.format("%-140s", "")).append(String.format("%-35s", ""))
                    .append(String.format("%-35s", "")).append(String.format("%-40s", ""))
                    .append(String.format("%-40s", ""));
                    
                    // Other Payment Reference 2
                    data.append(String.format("%-40s", ""));
                    
                    // Other Payment Reference 3(can't get it in excel)
                    data.append(String.format("%-40s", ""));
                    
                    // Transaction Description(direct get the value from Description column)
                    data.append(String.format("%-35s", description.substring(1)));
                    
                    // Channel
                    if (description.contains("Cash")) {
                        data.append(String.format("%-20s", "ATM"));
                    } else if (description.contains("IBG")) {
                        data.append(String.format("%-20s", "IBG"));
                    } else {
                        data.append(String.format("%-20s", "BPS"));
                    }
                    data.append(String.format("%-441s", "")).append("\r\n");
                } else {
                    startAtSecond = true;
                }
            }
            br.close();
            
            // Trailer
            data.append("9").append(String.format("%09d", count)).append("000000000")
            .append(String.format("%017d", Integer.parseInt(total.setScale(0, RoundingMode.FLOOR).toString())));
            
            // decimal of total
            BigDecimal decimalOfTotal = total.setScale(2, RoundingMode.HALF_UP);
            BigDecimal decimalToInt = decimalOfTotal.subtract(decimalOfTotal.setScale(0, RoundingMode.FLOOR)).movePointRight(decimalOfTotal.scale());
            if (decimalToInt.compareTo(BigDecimal.ZERO) == 0) {
                data.append("00");
            } else {
                data.append(String.format("%-2s", decimalToInt.toString()));
            }
            data.append(String.format("%019d", 0)).append(String.format("%-943s", ""));
            
            // print out result
            System.out.println(data);
            
            // file name
            String fn = "VA1" + new SimpleDateFormat("yyyyMMdd").format(new Date());
            
            // Please modify below path to your Desktop path
            String path = "C:\\Users\\Koon Fung Yee\\Desktop\\" + fn + ".txt";
            writer1 = new BufferedWriter(new FileWriter(path));
            writer1.write(data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer1 != null) {
                    writer1.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/uploadSingleQudoteCSV", method = { RequestMethod.POST, RequestMethod.GET })
    public void uploadSingleQudoteCSV(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        File cacheFile = null;
        BufferedWriter writer1 = null;
        try {
            // Convert MultipartFile to File
            cacheFile = File.createTempFile(file.getOriginalFilename(), ".xlsx");
            file.transferTo(cacheFile);

            // header
            StringBuffer data = new StringBuffer();
            data.append("1").append(String.format("%-19s", "2263018755")).append("VA1")
            .append(new SimpleDateFormat("yyyyMMdd").format(new Date()))
            .append(new SimpleDateFormat("HHmmss").format(new Date())).append("MYR")
            .append(String.format("%-140s", "SHENMA CREDIT SDN BHD"))
            .append(new SimpleDateFormat("yyyyMMdd").format(new Date()))
            .append(String.format("%-812s", "")).append("\r\n");
            
            String line = "";  
            String splitBy = "\",\"";  
            BufferedReader br = new BufferedReader(new FileReader(cacheFile));
            Boolean startAtSecond = false;
            BigDecimal total = BigDecimal.valueOf(0);
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (startAtSecond) {
                    String[] info = line.split(splitBy);
                    String accountNumber = info[1].replaceAll("\"", "");
                    String date = info[3].replaceAll("\"", "");
                    String time = info[4].replaceAll("\"", "");
                    String description  = info[5].replaceAll("\"", "");
                    String yourReference  = info[6].replaceAll("\"", "").replaceAll(" ", "");
                    String deposit  = info[17].replaceAll("\"", "").replaceAll(" ", "");
                    
                    Date formatterDate = new SimpleDateFormat("dd/MM/yyyy").parse(date.replaceAll(" ", ""));
                    String yymmdd = new SimpleDateFormat("yyyyMMdd").format(formatterDate);
                    Date formatterTime = new SimpleDateFormat("hh:mm:ssaa").parse(time.replaceAll(" ", ""));
                    String hhmmss = new SimpleDateFormat("HHmmss").format(formatterTime);
                    count += 1;
                    
                    data.append("2").append(yymmdd).append(hhmmss)
                    .append(String.format("%-34s", new BigDecimal(yourReference).setScale(0).toString()))
                    .append(String.format("%-6s", "")).append(String.format("%-34s", "")).append(String.format("%-6s", ""))
                    .append("C").append("T").append("MYR");
                    
                    // amount
                    BigDecimal depositAmt = new BigDecimal(deposit).setScale(0, RoundingMode.FLOOR);
                    
                    // decimal
                    BigDecimal amount = new BigDecimal(deposit).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal decimalToInt = amount.subtract(amount.setScale(0, RoundingMode.FLOOR)).movePointRight(amount.scale());
                    total = total.add(amount);
                    
                    data.append(String.format("%015d", Integer.parseInt(depositAmt.toString())));
                    if (decimalToInt.compareTo(BigDecimal.ZERO) == 0) {
                        data.append(String.format("%-2s", "00"));
                    } else {
                        data.append(String.format("%-2s", decimalToInt.toString()));
                    }
                    data.append(String.format("%015d", Integer.parseInt(depositAmt.toString())));
                    if (decimalToInt.compareTo(BigDecimal.ZERO) == 0) {
                        data.append(String.format("%-2s", "00"));
                    } else {
                        data.append(String.format("%-2s", decimalToInt.toString()));
                    }
                    
                    // Client Account Name(can't get it in excel)
                    data.append(String.format("%-140s", "")).append(String.format("%-35s", ""))
                    .append(String.format("%-35s", "")).append(String.format("%-40s", ""))
                    .append(String.format("%-40s", ""));
                    
                    // Other Payment Reference 2
                    data.append(String.format("%-40s", ""));
                    
                    // Other Payment Reference 3(can't get it in excel)
                    data.append(String.format("%-40s", ""));
                    
                    // Transaction Description(direct get the value from Description column)
                    data.append(String.format("%-35s", description.substring(1)));
                    
                    // Channel
                    if (description.contains("Cash")) {
                        data.append(String.format("%-20s", "ATM"));
                    } else if (description.contains("IBG")) {
                        data.append(String.format("%-20s", "IBG"));
                    } else {
                        data.append(String.format("%-20s", "BPS"));
                    }
                    data.append(String.format("%-441s", "")).append("\r\n");
                } else {
                    startAtSecond = true;
                }
            }
            br.close();
            
            // Trailer
            data.append("9").append(String.format("%09d", count)).append("000000000")
            .append(String.format("%017d", Integer.parseInt(total.setScale(0, RoundingMode.FLOOR).toString())));
            
            // decimal of total
            BigDecimal decimalOfTotal = total.setScale(2, RoundingMode.HALF_UP);
            BigDecimal decimalToInt = decimalOfTotal.subtract(decimalOfTotal.setScale(0, RoundingMode.FLOOR)).movePointRight(decimalOfTotal.scale());
            if (decimalToInt.compareTo(BigDecimal.ZERO) == 0) {
                data.append("00");
            } else {
                data.append(String.format("%-2s", decimalToInt.toString()));
            }
            data.append(String.format("%019d", 0)).append(String.format("%-943s", ""));
            
            // print out result
            System.out.println(data);
            
            // file name
            String fn = "VA1" + new SimpleDateFormat("yyyyMMdd").format(new Date());
            
            // Please modify below path to your Desktop path
            String path = "C:\\Users\\Koon Fung Yee\\Desktop\\" + fn + ".txt";
            writer1 = new BufferedWriter(new FileWriter(path));
            writer1.write(data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer1 != null) {
                    writer1.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
