package planned.it.ulli.ulli_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UlliBeApplication {

	public static void main(String[] args) {
//		System.setProperty("io.netty.transport.noNative", "false");
		SpringApplication.run(UlliBeApplication.class, args);
	}
}
