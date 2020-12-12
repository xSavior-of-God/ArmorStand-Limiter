package com.xSavior_of_God.ArmorStandLimiter.notifications;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Location;

import com.xSavior_of_God.ArmorStandLimiter.Utilis;

public class Telegram {
	private static String message;
	private static String chatID;
	private static URL url;

	
	public Telegram(final String API, final String TOKEN, final String MESSAGE, final String CHAT_ID) {
		Telegram.message = MESSAGE;
		Telegram.chatID = CHAT_ID;
		try {
			url = new URL(API + TOKEN + "/sendMessage");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public static void messageBuilder(final Location LOC, final int COUNTER, final int LIMIT) {
		final double X = LOC.getX();
		final double Z = LOC.getZ();
		final String WORLD = LOC.getWorld().getName();
		final String MESS = message.replace("{x}", X + "").replace("{z}", Z + "").replace("{world}", WORLD)
		    .replace("{counter}", COUNTER + "").replace("{max}", LIMIT + "");
		sendMessage(MESS);
	}

	public static void sendMessage(String text) {
		Map<String, Object> parameters = new LinkedHashMap<>();
		parameters.put("chat_id", chatID);
		parameters.put("text", text);
		parameters.put("parse_mode", "HTML");
		try {
			Utilis.apiRequest(parameters, url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
