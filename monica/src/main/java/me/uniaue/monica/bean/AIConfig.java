package me.uniaue.monica.bean;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;

import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;


/**
 * @author lyx
 * @date 2025/3/19 上午11:00
 */
@Configuration
public class AIConfig {

    @Bean
    ChatMemory chatMemory(){
        return new InMemoryChatMemory();
    }

    @Bean
    ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        return builder.defaultSystem("你是一个天气查询小助手")
                //设置上下文记忆
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }

    @Bean
    @Description("当询问某个地方的天气时候")
    Function<WeatherFunction.ChatRequest, WeatherFunction.ChatResponse> weatherFunction(){
        return new WeatherFunction();
    }
}
