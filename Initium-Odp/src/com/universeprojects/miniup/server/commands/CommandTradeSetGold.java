package com.universeprojects.miniup.server.commands;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.universeprojects.cacheddatastore.CachedDatastoreService;
import com.universeprojects.cacheddatastore.CachedEntity;
import com.universeprojects.miniup.server.NotificationType;
import com.universeprojects.miniup.server.ODPDBAccess;
import com.universeprojects.miniup.server.TradeObject;
import com.universeprojects.miniup.server.commands.framework.Command;
import com.universeprojects.miniup.server.commands.framework.UserErrorMessage;

public class CommandTradeSetGold extends Command {
	
	public CommandTradeSetGold(HttpServletRequest request, HttpServletResponse response)
	{
		super(request, response);
	}
	
	public void run(Map<String,String> parameters) throws UserErrorMessage {
		
		ODPDBAccess db = getDB();
		CachedDatastoreService ds = getDS();
		TradeObject tradeObject = TradeObject.getTradeObjectFor(ds, db.getCurrentCharacter(request));
		String dogecoinStr = parameters.get("amount");
		Long characterId = tryParseId(parameters,"characterId");
		CachedEntity otherCharacter = db.getEntity("Character", characterId);
        
        Long dogecoin = null;
        try {
        	dogecoinStr = dogecoinStr.replace(",", "");
            dogecoin = Long.parseLong(dogecoinStr);
        	}
        	catch (Exception e){
        		new UserErrorMessage("Please type a valid gold amount.");
        	}
        	
        
        db.setTradeDogecoin(ds, db.getCurrentCharacter(request), dogecoin);
        db.sendNotification(ds,otherCharacter.getKey(),NotificationType.tradeChanged);
        
        Integer tradeVersion = tradeObject.getVersion();
        addCallbackData("tradeVersion",tradeVersion);
	}
}