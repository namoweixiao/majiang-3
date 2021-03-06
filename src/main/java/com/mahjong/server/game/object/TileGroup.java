package com.mahjong.server.game.object;

import java.io.Serializable;
import java.util.Arrays;

import com.mahjong.server.game.enums.PlayerLocation.Relation;

/**
 * 牌组，即玩家的牌中除活牌外的若干个组，通常是吃、碰、杠等动作形成。
 * 
 */
public class TileGroup implements Serializable {
	private static final long serialVersionUID = 1L;

	private TileGroupType type;
	private Tile tile;
	private Relation fromRelation;
	private Tile gotTile;
	
	private Integer tileGroupTypeScore;

	/**
	 * 新建一个实例。
	 * 
	 * @param type
	 *            类型
	 * @param gotTile
	 *            得牌
	 * @param fromRelation
	 *            得牌来自于哪个关系的玩家
	 * @param tile
	 *            牌组中的牌
	 * @throws IllegalArgumentException
	 *             不合法
	 */
	public TileGroup(TileGroupType type, Tile gotTile, Relation fromRelation,
			Tile tile) {
		if (!type.isLegalTile(tile))
			throw new IllegalArgumentException("Illegal group tile：[" + type + "]" + Arrays.asList(tile));

		this.type = type;
		this.gotTile = gotTile;
		this.fromRelation = fromRelation;
		this.tile = new Tile();
	}

	/**
	 * 新建一个实例，没有从其他玩家得到的牌。
	 * 
	 * @param type
	 *            类型
	 * @param tile
	 *            牌组中的牌
	 * @throws IllegalArgumentException
	 *             不合法
	 */
	public TileGroup(TileGroupType type, Tile tile) {
		this(type, null, null, tile);
	}

	/**
	 * 返回类型。
	 * 
	 * @return 类型
	 */
	public TileGroupType getType() {
		return type;
	}

	/**
	 * 返回牌组中所有牌。
	 * 
	 * @return tile 集合
	 */
	public Tile getTile() {
		return tile;
	}

	/**
	 * 返回得牌来自于哪个关系的玩家。
	 * 
	 * @return 玩家位置
	 */
	public Relation getFromRelation() {
		return fromRelation;
	}

	/**
	 * 返回得牌。
	 * 
	 * @return 得牌
	 */
	public Tile getGotTile() {
		return gotTile;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gotTile == null) ? 0 : gotTile.hashCode());
		result = prime * result
				+ ((fromRelation == null) ? 0 : fromRelation.hashCode());
		result = prime * result + ((tile == null) ? 0 : tile.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TileGroup))
			return false;
		TileGroup other = (TileGroup) obj;
		if (gotTile == null) {
			if (other.gotTile != null)
				return false;
		} else if (!gotTile.equals(other.gotTile))
			return false;
		if (fromRelation == null) {
			if (other.fromRelation != null)
				return false;
		} else if (!fromRelation.equals(other.fromRelation))
			return false;
		if (tile == null) {
			if (other.tile != null)
				return false;
		} else if (!tile.equals(other.tile))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	/**
	 * Just for debug.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + type + " " + gotTile + " from " + fromRelation
				+ " to compose " + tile + "]";
	}

	public Integer getTileGroupTypeScore() {
		return tileGroupTypeScore;
	}

	public void setTileGroupTypeScore(Integer tileGroupTypeScore) {
		this.tileGroupTypeScore = tileGroupTypeScore;
	}

}
