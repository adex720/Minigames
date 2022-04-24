package io.github.adex720.minigames.util.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpsRequester {

    public JsonElement requestJson(String request) throws IOException {
        return JsonParser.parseString(requestString(request));
    }

    public String requestString(String request) throws IOException {
        URL url = new URL(request);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        InputStream ins = connection.getInputStream();
        InputStreamReader isr = new InputStreamReader(ins);
        BufferedReader in = new BufferedReader(isr);
        String inputLine;

        StringBuilder stringBuilder = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            stringBuilder.append(inputLine);
        }

        return stringBuilder.toString();
    }

    /**
     * Makes a request to the Discord api.
     *
     * @param jda jda
     * @param dataConvertor Data convertor which converts {@link DataObject}s to correct type.
     * @param <T> Type if value to return.
     * @return List of the values.
     * @throws RateLimitedException When request surpasses ratelimit.
     * @throws IllegalThreadStateException When the running thread is main or command listener.
     */
    public <T> List<T> makeDiscordApiRequest(JDA jda, DataConvertor<T> dataConvertor, Route.CompiledRoute route) throws RateLimitedException, IllegalThreadStateException {
        String threadName = Thread.currentThread().getName();

        if (threadName.equals("main") || threadName.equals("listener-1")) {
            throw new IllegalThreadStateException("HttpsRequester#makeDiscordApiRequest must not be called with an important thread since it waits until the request is finished.");
        }

        RestActionImpl<List<T>> restAction = new RestActionImpl<>(jda, route,
                (response, request) -> {
                    DataArray dataArray = response.getArray();
                    System.out.println("data: " + dataArray);
                    return dataArray
                            .stream(DataArray::getObject)
                            .map(dataConvertor::fromJson)
                            .collect(Collectors.toList());
                });

        List<T> connectionApps = new ArrayList<>();
        restAction.queue(connectionApps::addAll);
        restAction.complete(false);

        return connectionApps;
    }

    public interface DataConvertor<T> {
        T fromJson(DataObject dataObject);
    }

}
