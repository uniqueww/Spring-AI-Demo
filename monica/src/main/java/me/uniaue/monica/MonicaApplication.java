package me.uniaue.monica;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class MonicaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonicaApplication.class, args);
    }


    @Bean
    CommandLineRunner runner() {
        return args -> {
            System.out.println("Hello Monica!");
            String proxy = "127.0.0.1";
            int port = 7897;
            System.setProperty("proxyType", "4");
            System.setProperty("proxyPort", Integer.toString(port));
            System.setProperty("proxyHost", proxy);
            System.setProperty("proxySet", "true");
        };
    }

}
