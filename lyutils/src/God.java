import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.text.DateFormatter;

public class God {
	public static void main(String[] args) {
		System.out.println(123132);
//		System.out.println(new Date());
		DateFormat format = DateFormat.getTimeInstance();
		System.out.println(format.format(new Date()));
		SimpleDateFormat ss = new SimpleDateFormat();
		String d = ss.format(new Date());
		System.out.println(d);
		DateFormatter a = new DateFormatter();
		String adf = a.getFormat().format(new Date());
		System.out.println(adf);
		String vc = Calendar.getInstance().toString();
		System.out.println(vc);
		System.out.println(new java.sql.Date(new Date().getTime()));
		String rr = DateFormat.getDateTimeInstance().format(new Date());
		System.out.println(rr);
	}
}
