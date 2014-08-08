package org.game.knight.version.packer.world.model;

import java.io.File;


public class Scene
{
	public final File file;
	public final int sceneID;
	public final String sceneName;
	public final int sceneGroup;
	public final int sceneType;
	public final int timeLimit;
	public final int sceneW;
	public final int sceneH;
	public final int beginX;
	public final int defaultX;
	public final int defaultY;
	public final int viewX;
	public final int viewY;
	public final String grid;
	public final ProjectMp3File bgs;
	public final SceneSection[] sections;
	public final SceneBackLayer[] backLayers;
	public final SceneForeLayer[] foreLayers;
	public final SceneAnim[] backAnims;
	public final SceneAnim[] anims;
	public final SceneNpc[] npcs;
	public final SceneDoor[] doors;
	public final ScenePart[] parts;
	public final SceneTrap[] traps;

	/**
	 * ¹¹Ôìº¯Êý
	 * @param file
	 * @param id
	 * @param name
	 * @param group
	 * @param type
	 * @param limit
	 * @param sceneW
	 * @param sceneH
	 * @param beginX
	 * @param defX
	 * @param defY
	 * @param viewX
	 * @param viewY
	 * @param grid
	 * @param mp3
	 * @param sections
	 * @param backLayers
	 * @param foreLayers
	 * @param backAnims
	 * @param foreAnims
	 * @param npcs
	 * @param doors
	 * @param parts
	 * @param traps
	 */
	public Scene(ProjectFile file,int id,String name,int group,int type,int limit,int sceneW,int sceneH,int beginX,int defX,int defY,int viewX,int viewY,String grid,ProjectMp3File mp3,SceneSection[] sections,SceneBackLayer[] backLayers,SceneForeLayer[] foreLayers,SceneAnim[] backAnims,SceneAnim[] foreAnims,SceneNpc[] npcs,SceneDoor[] doors,ScenePart[] parts,SceneTrap[] traps)
	{
		this.file=file;
		this.sceneID=id;
		this.sceneName=name;
		this.sceneGroup=group;
		this.sceneType=type;
		this.timeLimit=limit;
		this.sceneW=sceneW;
		this.sceneH=sceneH;
		this.beginX=beginX;
		this.defaultX=defX;
		this.defaultY=defY;
		this.viewX=viewX;
		this.viewY=viewY;
		this.grid=grid;
		this.bgs=mp3;
		this.sections=sections;
		this.backLayers=backLayers;
		this.foreLayers=foreLayers;
		this.backAnims=backAnims;
		this.anims=foreAnims;
		this.npcs=npcs;
		this.doors=doors;
		this.parts=parts;
		this.traps=traps;
	}
}