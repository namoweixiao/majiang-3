package com.mahjong.server.game.context;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.mahjong.server.entity.UserInfo;
import com.mahjong.server.game.enums.PlayerLocation;
import com.mahjong.server.game.enums.RoomStatus;
import com.mahjong.server.game.object.MahjongTable;
import com.mahjong.server.game.object.PlayerInfo;
import com.mahjong.server.game.rule.FangKa;
import com.mahjong.server.game.rule.GameStrategy;
import com.mahjong.server.game.rule.RuleInfo;

public class HouseContext {

	public static ConcurrentHashMap<Integer, RoomContext> rommList = new ConcurrentHashMap<Integer, RoomContext>();
	
	public static ConcurrentHashMap<String, RoomContext> weixinIdToRoom = new ConcurrentHashMap<String, RoomContext>();

	public RoomContext getRoomByNum(Integer roomNum) {
		return rommList.get(roomNum);
	}

	public boolean isRoomExits(Integer roomNum) {
		return rommList.containsKey(roomNum);
	}

	public static RoomContext addRoom(UserInfo creator, String ruleStr,int fangKaJuShu) {
		RoomContext roomContext = new RoomContext();
		
		Integer roomNum = getRoomNum();
		roomContext = rommList.putIfAbsent(roomNum, roomContext);
		while(roomContext.getGameContext() != null){
			 roomContext = rommList.putIfAbsent(roomNum, roomContext);
		}
	   
	    roomContext.setRoomNum(roomNum);
	   
	    MahjongTable table = new MahjongTable();
	    table.init();
	    PlayerInfo creatorPlayer = new PlayerInfo();
	    creatorPlayer.setUserInfo(creator);
	    creatorPlayer.setCurScore(1000);
	    creatorPlayer.setUserLocation(PlayerLocation.EAST.getCode());
	    table.setPlayer(PlayerLocation.EAST, creatorPlayer);
	   
	    GameStrategy gameStrategy = new GameStrategy();
	    RuleInfo ruleInfo = new RuleInfo();
	    ruleInfo.parseRuleFromBitString(ruleStr);
	    ruleInfo.setFangKa(FangKa.fromCode(fangKaJuShu));
	    gameStrategy.setRuleInfo(ruleInfo);
	   
	    GameContext gameContext = new GameContext(table, gameStrategy);
	   
	    roomContext.setGameContext(gameContext);
	    roomContext.setRoomStatus(RoomStatus.WAIT_FOR_READY);
	   
		return roomContext;

	}

	private static int getRoomNum(){
		String roomNum = "";
		for(int i=0;i<6;i++){
			Random random = new Random();
			int eachNum = random.nextInt(10);
			roomNum += eachNum;
		}
		return Integer.parseInt(roomNum);
	}

}