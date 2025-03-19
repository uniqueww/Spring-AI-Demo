package me.uniaue.monica.bean;


import java.util.function.Function;

/**
 * @author lyx
 * @date 2025/3/19 下午3:46
 */
public class WeatherFunction implements Function<WeatherFunction.ChatRequest, WeatherFunction.ChatResponse> {


    @Override
    public ChatResponse apply(ChatRequest chatRequest) {
        if (chatRequest.location==null){
            return new ChatResponse("参数缺失无法，无法调用function-call");
        }
        return new ChatResponse("温度10，湿度50%，天气晴朗，14点后多云转晴");
    }


    public record ChatRequest(String location){

    }

    public record ChatResponse(String message){

    }

}
