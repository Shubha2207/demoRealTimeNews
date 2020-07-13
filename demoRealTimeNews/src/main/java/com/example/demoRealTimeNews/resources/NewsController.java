package com.example.demoRealTimeNews.resources;


import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.tomcat.util.http.parser.MediaType;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class NewsController {

    //public List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public Map<String, SseEmitter> emitters = new HashMap<>();


    //method for subscription
    @CrossOrigin
    @RequestMapping(value = "/subscribe")
    public SseEmitter subscribe(@RequestParam String UserID) {

        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        sendInitEvent(sseEmitter);

        //emitters.add(sseEmitter);
        emitters.put(UserID,sseEmitter);

        sseEmitter.onCompletion(()->emitters.remove(sseEmitter));
        sseEmitter.onTimeout(()->emitters.remove(sseEmitter));
        sseEmitter.onError((e)->emitters.remove(sseEmitter));

        return sseEmitter;
    }

    private void sendInitEvent(SseEmitter sseEmitter){
        try {
            sseEmitter.send(SseEmitter.event().name("INIT"));
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    //method for dispatching events to all clients
    //to specific user
    @PostMapping(value = "/dispatchEvent")
    public void dispatchEventToSpecificClient(@RequestParam String title,@RequestParam String text, @RequestParam String UserID) throws JSONException {

        String eventFormatted = new JSONObject()
                .put("title",title)
                .put("text",text).toString();

        SseEmitter sseEmitter = emitters.get(UserID);
        if(sseEmitter != null){
            try{
                sseEmitter.send(SseEmitter.event().name("latestNews").data(eventFormatted));
            }catch (IOException e){
                emitters.remove(sseEmitter);
                //e.printStackTrace();
            }

        }

//        for( SseEmitter emitter : emitters){
//            try{
//                emitter.send(SseEmitter.event().name("latestNews").data(eventFormatted));
//            }catch (IOException e){
//                emitters.remove(emitter);
//                //e.printStackTrace();
//            }
//        }

    }

}