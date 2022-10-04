package indocyber.exam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class ExamHotelApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExamHotelApplication.class, args);
	}

}
