package me.uniaue.monica.service;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author lyx
 * @date 2025/3/19 上午10:59
 */
@RestController
public class ChatAIService {

    @Resource
    public ChatClient chatClient;

    @Resource
    public ChatModel chatModel;

    @Resource
    public ImageModel imageModel;

    @Resource
    public OpenAiAudioSpeechModel ttsModel;


    @GetMapping(value = "/ai/client", produces = "text/html;charset=UTF-8")
    public Flux<String> clientChat(@RequestParam(value = "message", defaultValue = "昆明今天是天气如何？") String message) {
        return chatClient.prompt()
                .user(message)
                .advisors(a -> {
                    //设置上下文记忆长度
                    a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100);
                })
                .stream()
                .content();
    }

    @GetMapping("/ai/chat")
    public String modelChat(@RequestParam(value = "message", defaultValue = "今天出门需要注意什么？") String message) {
        ChatResponse chatResponse = chatModel.call(
                new Prompt(message,
                        OpenAiChatOptions.builder()
                                .model("deepseek-reasoner")
                                .temperature(0.4)
                                .build()
                ));
        return chatResponse.getResult().getOutput().getText();
    }

    @GetMapping("/ai/image")
    public String modelImage(@RequestParam(value = "desc", defaultValue = "画一只猫咪") String desc) {
        ImageResponse hd = imageModel.call(new ImagePrompt(
                desc, OpenAiImageOptions.builder()
                .withModel(OpenAiImageApi.DEFAULT_IMAGE_MODEL)
                .withQuality("hd")
                //Must be one of 256x256, 512x512, or 1024x1024 for dall-e-2.
                //Must be one of 1024x1024, 1792x1024, or 1024x1792 for dall-e-3 models.
                .withHeight(1024)
                .withWidth(1024)
                .build()
        ));
        return hd.getResult().getOutput().getUrl();
    }

    @GetMapping("/ai/tts")
    public String modelTts(@RequestParam(value = "desc", defaultValue = "憾无穷，人生长恨水长东") String desc) {
        SpeechResponse speechResponse = ttsModel.call(new SpeechPrompt(
                desc,
                OpenAiAudioSpeechOptions.builder()
                        //转义模型
                        .model(OpenAiAudioApi.TtsModel.TTS_1.value)
                        //输出的类型
                        .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                        .speed(1F)
                        //声音的音色
                        .voice(OpenAiAudioApi.SpeechRequest.Voice.ONYX)
                        .build()
        ));
        byte[] output = speechResponse.getResult().getOutput();
        // 指定输出的 MP3 文件路径
        String outputFilePath = System.getProperty("user.dir") + File.separator + "output.mp3";
        try {
            try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                fos.write(output);
            }
            System.out.println("MP3 文件已成功生成: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("生成 MP3 文件时出错: " + e.getMessage());
        }
        return "ok";
    }

    @GetMapping(value = "/ai/multimodality" , produces = "text/html;charset=UTF-8")
    public Flux<String> modelMultimodality(@RequestParam(value = "desc", defaultValue = "在长张图中你观察到了什么？") String desc) {

        Flux<String> content = ChatClient.create(chatModel).prompt()
                .user(u -> u.text(desc)//文本描述
                        .media(MimeTypeUtils.IMAGE_PNG, new ClassPathResource("/static/multimodal.test.png")))
                .stream()
                .content();
        return content;
    }

    @GetMapping("/ai/funCall")
    public String modelFunCall(@RequestParam(value = "message", defaultValue = "昆明今天的天气怎么样？") String message) {
        return chatClient.prompt()
                .user(message)
                .functions("weatherFunction")
                .advisors(a -> {
                    //设置上下文记忆长度
                    a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100);
                })
                .call()
                .content();
    }

}
