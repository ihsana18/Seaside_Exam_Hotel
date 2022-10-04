package indocyber.exam.utility;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Helper {
    public static String formatTanggalIndonesia(LocalDate tanggal){
        Locale indo = new Locale("id","ID");
        DateTimeFormatter indoformat = DateTimeFormatter.ofPattern("dd MMMM yyyy", indo);
        return indoformat.format(tanggal);
    }

    public static String formatTanggal(LocalDate tanggal){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return formatter.format(tanggal);
    }

    public static String formatRupiah(BigDecimal uang){
        Locale indo = new Locale("id","ID");
        return NumberFormat.getCurrencyInstance(indo).format(uang);
    }
}
