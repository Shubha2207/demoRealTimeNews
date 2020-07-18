package com.example.demoRealTimeNews.resources;


import com.example.demoRealTimeNews.models.Event;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class NewsController {

    public List<SseEmitter> emitters = new CopyOnWriteArrayList<>();



    //public Map<String, SseEmitter> emitters = new HashMap<>();


    //method for subscription
    @CrossOrigin
    @RequestMapping(value = "/subscribe")
    public SseEmitter subscribe()  {

        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        sendInitEvent(sseEmitter);

        emitters.add(sseEmitter);
        //emitters.put(UserID,sseEmitter);


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
    @PostMapping(value = "/dispatchEvent", consumes = "application/json", produces = "application/json")
    public void dispatchEventToAllClients(@RequestBody String eventString) throws JSONException, InterruptedException {

        JSONObject root = new JSONObject(eventString);

        List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        String headerValue="", bodyValue="";

        Event eventObj = new Event();
        eventObj.setEventName(root.get("EventName").toString());

        JSONArray plants = root.getJSONArray("EventData");

        System.out.println(plants);

        for(int i=0;i<plants.length();i++){
            JSONObject jsonplant = plants.getJSONObject(i);
            headerValue = jsonplant.getString("headerValue");
            bodyValue = jsonplant.getString("bodyValue");

            map.put("headerValue",headerValue);
            map.put("bodyValue",bodyValue);

            list.add(map);
        }

        eventObj.setEventData(list);

        for( SseEmitter emitter : emitters){
            try{
                emitter.send(SseEmitter.event().name(eventObj.getEventName()).data(eventObj.getEventData()));
            }catch (IOException e){
                emitters.remove(emitter);
                //e.printStackTrace();
            }
        }

    }

}