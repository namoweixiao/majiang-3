package com.mahjong.server.game.object;

import java.util.Set;

import com.mahjong.server.game.GameContext;
import com.mahjong.server.game.action.Action;
import com.mahjong.server.game.action.ActionType;
import com.mahjong.server.game.action.PlayerLocation;

/**
 * 玩家。
 * 
 */
public interface Player {
	/**
	 * 返回玩家名字。
	 */
	public String getName();

	/**
	 * 选择一个要执行的动作。选择过程中需要检查线程中断，如果被中断则不需要继续选择（可能是限时已到，或其他玩家已做出优先级更高的动作等），
	 * 此时抛出InterruptedException即可。
	 * 
	 * @param contextView
	 *            游戏上下文
	 * @param actionTypes
	 *            可选动作类型
	 * @param illegalAction
	 *            如果非null，则表示上一次选择的动作不符合规则，需要重新选择。此参数提供上次选择的动作。
	 * @return 要执行的动作
	 * @throws InterruptedException
	 *             线程被中断时抛出此异常。选择过程中随时可能被中断，实现时应该经常检查。
	 */
	public Action chooseAction(GameContext.PlayerView contextView,
			Set<ActionType> actionTypes, Action illegalAction)
			throws InterruptedException;

	/**
	 * 完成一个动作时通知。
	 */
	void actionDone(GameContext.PlayerView contextView,
			PlayerLocation actionLocation, Action action);
}