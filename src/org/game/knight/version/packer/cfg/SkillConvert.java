package org.game.knight.version.packer.cfg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.chw.util.XmlUtil;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class SkillConvert extends DefaultHandler
{
	private boolean skillsing;
	private boolean groupsing;
	private boolean labelsing;

	private float downSpeed;

	public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws org.xml.sax.SAXException
	{
		if (skillsing)
		{
			startSkill(uri, localName, qName, attributes);
		}
		else if (groupsing)
		{
			startGroup(uri, localName, qName, attributes);
		}
		else if (labelsing)
		{
			startLabel(uri, localName, qName, attributes);
		}
		else
		{
			if ("skills".equals(qName))
			{
				skillsing = true;
				downSpeed = XmlUtil.parseFloat(attributes.getValue("downSpeed"), 0.0026f);
			}
			else if ("groups".equals(qName))
			{
				groupsing = true;
			}
			else if ("labels".equals(qName))
			{
				labelsing = true;
			}
		}
	};

	public void endElement(String uri, String localName, String qName) throws org.xml.sax.SAXException
	{
		if (skillsing)
		{
			if ("skills".equals(qName))
			{
				skillsing = false;
			}
			else
			{
				endSkill(uri, localName, qName);
			}
		}
		else if (groupsing)
		{
			if ("groups".equals(qName))
			{
				groupsing = false;
			}
			else
			{
				endGroup(uri, localName, qName);
			}
		}
		else if (labelsing)
		{
			if ("labels".equals(qName))
			{
				labelsing = false;
			}
			else
			{
				endLabel(uri, localName, qName);
			}
		}
	};

	/**
	 * 生成
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void build(File file) throws ParserConfigurationException, SAXException, FileNotFoundException, IOException
	{
		if (file.exists() && file.isFile())
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();

			parser.parse(new FileInputStream(file), this);
		}
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException
	{
		throw new SAXException("致命错误：" + e.getLineNumber() + "行" + e.getColumnNumber() + "列" + "   " + e.getMessage(), e);
	}

	@Override
	public void error(SAXParseException e) throws SAXException
	{
		throw new SAXException("错误：" + e.getLineNumber() + "行" + e.getColumnNumber() + "列" + "   " + e.getMessage(), e);
	}

	/**
	 * 获取内容
	 * 
	 * @return
	 */
	public String getContent()
	{
		return String.format("<root downSpeed=\"%s\">\n", downSpeed) + getSkillXML() + getAllSkillGroups() + getAllLabelXML() + "</root>";
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------
	//
	// 技能重构
	//
	// -----------------------------------------------------------------------------------------------------------------------------------------

	private boolean attacking;
	private boolean hurtering;
	private boolean bulleting;

	private int motionNodeID = 0;
	private Hashtable<String, Integer> motionNodePool = new Hashtable<String, Integer>();
	private int bulletNodeID = 0;
	private Hashtable<String, Integer> bulletNodePool = new Hashtable<String, Integer>();
	private int quiverNodeID = 0;
	private Hashtable<String, Integer> quiverNodePool = new Hashtable<String, Integer>();
	private int hitEffectNodeID = 0;
	private Hashtable<String, Integer> hitEffectNodePool = new Hashtable<String, Integer>();
	private int hitNodeID = 0;
	private Hashtable<String, Integer> hitNodePool = new Hashtable<String, Integer>();
	private int skillNodeID = 0;
	private Hashtable<String, Integer> skillNodePool = new Hashtable<String, Integer>();

	private Skill skill;
	private SkillData skillData;

	private SkillBullet bullet;
	private SkillHit hit;
	private ArrayList<Skill> skills = new ArrayList<Skill>();
	private Hashtable<String, Skill> skillHash = new Hashtable<String, Skill>();

	/**
	 * 开始技能
	 * 
	 * @param uri
	 * @param localName
	 * @param qName
	 * @param attributes
	 * @throws org.xml.sax.SAXException
	 */
	private void startSkill(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws org.xml.sax.SAXException
	{
		if ("skill".equals(qName))
		{
			skill = new Skill();
			skill.skillID = XmlUtil.parseInt(attributes.getValue("id"), 0);
			skill.skillLv = XmlUtil.parseInt(attributes.getValue("lvl"), 0);
			skills.add(skill);
			skillHash.put(skill.skillID + "_" + skill.skillLv, skill);

			skillData = new SkillData();
			skillData.skillCooldown = XmlUtil.parseInt(attributes.getValue("cooldown"), 0);
			skillData.skillIgnorePath = XmlUtil.parseInt(attributes.getValue("pathMode"), 0) == 2;
			skillData.skillPrevAction = XmlUtil.parseString(attributes.getValue("prevAction"), "");
		}
		else if ("attacker".equals(qName))
		{
			attacking = true;
		}
		else if ("hurter".equals(qName))
		{
			hurtering = true;
		}
		else if ("hitEffect".equals(qName))
		{
			if (attacking)
			{
				bulleting = true;

				bullet = new SkillBullet();
				bullet.delay = XmlUtil.parseInt(attributes.getValue("delay"), 0);
				bullet.attireId = filterAttireName(XmlUtil.parseString(attributes.getValue("attireId"), ""));
				bullet.offsetX = XmlUtil.parseInt(attributes.getValue("offsetX"), 0);
				bullet.offsetY = XmlUtil.parseInt(attributes.getValue("offsetY"), 0);
				bullet.offsetZ = XmlUtil.parseInt(attributes.getValue("offsetZ"), 0);
				bullet.duration = XmlUtil.parseInt(attributes.getValue("duration"), 0);
			}
			else
			{
				SkillHitEffect hitEffect = new SkillHitEffect();
				hitEffect.delay = XmlUtil.parseInt(attributes.getValue("delay"), 0);
				hitEffect.attireId = filterAttireName(XmlUtil.parseString(attributes.getValue("attireId"), ""));
				hitEffect.offsetX = XmlUtil.parseInt(attributes.getValue("offsetX"), 0);
				hitEffect.offsetY = XmlUtil.parseInt(attributes.getValue("offsetY"), 0);
				hitEffect.duration = XmlUtil.parseInt(attributes.getValue("duration"), 0);
				hitEffect.lockOwner=XmlUtil.parseBoolean(attributes.getValue("lockOwner"),false);

				String nodeHash = hitEffect.toString();
				if (!hitEffectNodePool.containsKey(nodeHash))
				{
					hitEffectNodeID++;
					hitEffectNodePool.put(nodeHash, hitEffectNodeID);
				}

				hit.hitEffect = hitEffectNodePool.get(nodeHash);
			}
		}
		else if ("motion".equals(qName))
		{
			SkillMotionNode node = new SkillMotionNode();
			node.distanceX = XmlUtil.parseInt(attributes.getValue("distanceX"), 0);
			node.distanceY = XmlUtil.parseInt(attributes.getValue("distanceY"), 0);
			node.distanceH = XmlUtil.parseInt(attributes.getValue("distanceZ"), 0);

			node.easingX = XmlUtil.parseInt(attributes.getValue("easingX"), 0);
			node.easingY = XmlUtil.parseInt(attributes.getValue("easingY"), 0);
			node.easingH = XmlUtil.parseInt(attributes.getValue("easingZ"), 0);

			node.actionID = XmlUtil.parseInt(attributes.getValue("actionId"), 0);

			node.duration = XmlUtil.parseInt(attributes.getValue("duration"), 0);
			node.stiffTime = XmlUtil.parseInt(attributes.getValue("stiffTime"), 0);

			node.moveMode = XmlUtil.parseInt(attributes.getValue("moveable"), 0);
			node.isAir = XmlUtil.parseInt(attributes.getValue("airAttack"), 0) == 1;
			node.speedX = XmlUtil.parseInt(attributes.getValue("speedX"), 0);

			String nodeHash = node.toString();
			if (!motionNodePool.containsKey(nodeHash))
			{
				motionNodeID++;
				motionNodePool.put(nodeHash, motionNodeID);
			}

			if (bulleting)
			{
				bullet.nodes.add(motionNodePool.get(nodeHash));
			}
			else if (attacking)
			{
				skillData.skillAttackPath.add(motionNodePool.get(nodeHash));
			}
			else if (hurtering)
			{
				hit.skillAttackPath.add(motionNodePool.get(nodeHash));
			}
		}
		else if ("attackerQuiver".equals(qName) || "hurterQuiver".equals(qName))
		{
			SkillQuiver quiver = new SkillQuiver();
			quiver.delayTime = XmlUtil.parseInt(attributes.getValue("delayTime"), 0);
			quiver.duration = XmlUtil.parseInt(attributes.getValue("duration"), 0);
			quiver.distanceX = XmlUtil.parseInt(attributes.getValue("distanceX"), 0);
			quiver.distanceY = XmlUtil.parseInt(attributes.getValue("distanceY"), 0);
			quiver.scale = XmlUtil.parseFloat(attributes.getValue("scale"), 0);
			quiver.interval = XmlUtil.parseInt(attributes.getValue("interval"), 0);

			String nodeHash = quiver.toString();
			if (!quiverNodePool.containsKey(nodeHash))
			{
				quiverNodeID++;
				quiverNodePool.put(nodeHash, quiverNodeID);
			}

			if (attacking)
			{
				skillData.skillQuiverList.add(quiverNodePool.get(nodeHash));
			}
			else if (hurtering)
			{
				hit.quiver = quiverNodePool.get(nodeHash);
			}
		}
		else if ("buff".equals(qName))
		{
			if (attacking)
			{
				skill.consumeBuffs.add(XmlUtil.parseInt(attributes.getValue("id"), 0));
			}
			else if (hurtering)
			{
				skill.gainBuffs.add(XmlUtil.parseInt(attributes.getValue("id"), 0));
			}
		}
		else if ("label".equals(qName))
		{
			if (hurtering)
			{
				skill.gainLabels.add(XmlUtil.parseInt(attributes.getValue("id"), 0));
			}
		}
		else if ("hit".equals(qName))
		{
			hit = new SkillHit();
			hit.hitID = XmlUtil.parseInt(attributes.getValue("innerId"), 0);
			hit.delayTime = XmlUtil.parseInt(attributes.getValue("delayTime"), 0);
			hit.offsetX = XmlUtil.parseInt(attributes.getValue("offsetX"), 0);
			hit.offsetY = XmlUtil.parseInt(attributes.getValue("offsetY"), 0);
			hit.offsetZ = XmlUtil.parseInt(attributes.getValue("offsetZ"), 0);
			hit.rangeX = XmlUtil.parseInt(attributes.getValue("rangeX"), 0);
			hit.rangeY = XmlUtil.parseInt(attributes.getValue("rangeY"), 0);
			hit.rangeZ = XmlUtil.parseInt(attributes.getValue("rangeZ"), 0);
			hit.deathSkillID = XmlUtil.parseInt(attributes.getValue("deathSkillID"), 0);
		}
	}

	/**
	 * 结束技能
	 * 
	 * @param uri
	 * @param localName
	 * @param qName
	 * @throws org.xml.sax.SAXException
	 */
	private void endSkill(String uri, String localName, String qName) throws org.xml.sax.SAXException
	{
		if ("skill".equals(qName))
		{
			String nodeHash = skillData.toString();
			if (!skillNodePool.containsKey(nodeHash))
			{
				skillNodeID++;
				skillNodePool.put(nodeHash, skillNodeID);
			}

			skill.skillDataID = skillNodePool.get(nodeHash);
		}
		else if ("attacker".equals(qName))
		{
			attacking = false;
		}
		else if ("hurter".equals(qName))
		{
			hurtering = false;
		}
		else if (attacking && "hitEffect".equals(qName))
		{
			bulleting = false;

			String nodeHash = bullet.toString();
			if (!bulletNodePool.containsKey(nodeHash))
			{
				bulletNodeID++;
				bulletNodePool.put(nodeHash, bulletNodeID);
			}

			skillData.skillBulletList.add(bulletNodePool.get(nodeHash));
		}
		else if (hurtering && "hit".equals(qName))
		{
			String nodeHash = hit.toString();
			if (!hitNodePool.containsKey(nodeHash))
			{
				hitNodeID++;
				hitNodePool.put(nodeHash, hitNodeID);
			}

			skillData.skillHitList.add(hitNodePool.get(nodeHash));
		}
	}

	/**
	 * 获取技能数据
	 * 
	 * @return
	 */
	private String getSkillXML()
	{

		// 排序技能
		Collections.sort(skills, new Comparator<Skill>()
		{
			@Override
			public int compare(Skill arg0, Skill arg1)
			{
				if (arg0.skillID < arg1.skillID)
				{
					return -1;
				}
				else if (arg0.skillID > arg1.skillID)
				{
					return 1;
				}
				else
				{
					if (arg0.skillLv < arg1.skillLv)
					{
						return -1;
					}
					else if (arg0.skillLv > arg1.skillLv)
					{
						return 1;
					}
				}
				return 0;
			}
		});

		StringBuilder txt = new StringBuilder();

		// 动画节点
		String[] nodes = motionNodePool.keySet().toArray(new String[motionNodePool.size()]);
		Arrays.sort(nodes, new Comparator<String>()
		{
			@Override
			public int compare(String o1, String o2)
			{
				int id1 = motionNodePool.get(o1);
				int id2 = motionNodePool.get(o2);
				if (id1 < id2)
				{
					return -1;
				}
				else if (id1 > id2)
				{
					return 1;
				}
				return 0;
			}
		});
		txt.append("\t<nodes>\n");
		for (String node : nodes)
		{
			txt.append("\t\t" + node.replaceFirst("\\<node", "<node id=\"" + motionNodePool.get(node) + "\"") + "\n");
		}
		txt.append("\t</nodes>\n");

		// 子弹节点
		String[] bullets = bulletNodePool.keySet().toArray(new String[bulletNodePool.size()]);
		Arrays.sort(bullets, new Comparator<String>()
		{
			@Override
			public int compare(String o1, String o2)
			{
				int id1 = bulletNodePool.get(o1);
				int id2 = bulletNodePool.get(o2);
				if (id1 < id2)
				{
					return -1;
				}
				else if (id1 > id2)
				{
					return 1;
				}
				return 0;
			}
		});
		txt.append("\t<bullets>\n");
		for (String bullet : bullets)
		{
			txt.append("\t\t" + bullet.replaceFirst("\\<bullet", "<bullet id=\"" + bulletNodePool.get(bullet) + "\"") + "\n");
		}
		txt.append("\t</bullets>\n");

		// 震动节点
		String[] quivers = quiverNodePool.keySet().toArray(new String[quiverNodePool.size()]);
		Arrays.sort(quivers, new Comparator<String>()
		{
			@Override
			public int compare(String o1, String o2)
			{
				int id1 = quiverNodePool.get(o1);
				int id2 = quiverNodePool.get(o2);
				if (id1 < id2)
				{
					return -1;
				}
				else if (id1 > id2)
				{
					return 1;
				}
				return 0;
			}
		});
		txt.append("\t<quivers>\n");
		for (String quiver : quivers)
		{
			txt.append("\t\t" + quiver.replaceFirst("\\<quiver", "<quiver id=\"" + quiverNodePool.get(quiver) + "\"") + "\n");
		}
		txt.append("\t</quivers>\n");

		// 打击效果节点
		String[] hitEffects = hitEffectNodePool.keySet().toArray(new String[hitEffectNodePool.size()]);
		Arrays.sort(hitEffects, new Comparator<String>()
		{
			@Override
			public int compare(String o1, String o2)
			{
				int id1 = hitEffectNodePool.get(o1);
				int id2 = hitEffectNodePool.get(o2);
				if (id1 < id2)
				{
					return -1;
				}
				else if (id1 > id2)
				{
					return 1;
				}
				return 0;
			}
		});
		txt.append("\t<hitEffect>\n");
		for (String hitEffect : hitEffects)
		{
			txt.append("\t\t" + hitEffect.replaceFirst("\\<effect", "<effect id=\"" + hitEffectNodePool.get(hitEffect) + "\"") + "\n");
		}
		txt.append("\t</hitEffect>\n");

		// 打击节点
		String[] hits = hitNodePool.keySet().toArray(new String[hitNodePool.size()]);
		Arrays.sort(hits, new Comparator<String>()
		{
			@Override
			public int compare(String o1, String o2)
			{
				int id1 = hitNodePool.get(o1);
				int id2 = hitNodePool.get(o2);
				if (id1 < id2)
				{
					return -1;
				}
				else if (id1 > id2)
				{
					return 1;
				}
				return 0;
			}
		});
		txt.append("\t<hits>\n");
		for (String hit : hits)
		{
			txt.append("\t\t" + hit.replaceFirst("\\<hit", "<hit id=\"" + hitNodePool.get(hit) + "\"") + "\n");
		}
		txt.append("\t</hits>\n");

		// 技能数据节点
		String[] skillDatas = skillNodePool.keySet().toArray(new String[skillNodePool.size()]);
		Arrays.sort(skillDatas, new Comparator<String>()
		{
			@Override
			public int compare(String o1, String o2)
			{
				int id1 = skillNodePool.get(o1);
				int id2 = skillNodePool.get(o2);
				if (id1 < id2)
				{
					return -1;
				}
				else if (id1 > id2)
				{
					return 1;
				}
				return 0;
			}
		});
		txt.append("\t<skillDatas>\n");
		for (String skill : skillDatas)
		{
			txt.append("\t\t" + skill.replaceFirst("\\<skillData", "<skillData id=\"" + skillNodePool.get(skill) + "\"") + "\n");
		}
		txt.append("\t</skillDatas>\n");

		txt.append("\t<skills>\n");
		for (Skill skill : skills)
		{
			txt.append(String.format("\t\t<skill id=\"%s\" lv=\"%s\" dataID=\"%s\" consumes=\"%s\" gainBuffs=\"%s\" gainLabels=\"%s\"/>\n", skill.skillID, skill.skillLv, skill.skillDataID, formatList(skill.consumeBuffs), formatList(skill.gainBuffs), formatList(skill.gainLabels)));
		}
		txt.append("\t</skills>\n");

		return txt.toString();
	}

	/**
	 * 格式化HashSet
	 * 
	 * @param ids
	 * @return
	 */
	private String formatList(HashSet<Integer> ids)
	{
		Integer[] ints = ids.toArray(new Integer[ids.size()]);
		Arrays.sort(ints);

		StringBuilder sb = new StringBuilder();
		for (int val : ints)
		{
			if (sb.length() > 0)
			{
				sb.append(",");
			}
			sb.append(val);
		}
		return sb.toString();
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------
	//
	// 技能组重构
	//
	// -----------------------------------------------------------------------------------------------------------------------------------------

	private int skillGroupID;
	private SkillGroupData skillGroupData;

	private int skillGroupDataNodeID = 0;
	private Hashtable<String, Integer> skillGroupDataNodePool = new Hashtable<String, Integer>();

	private ArrayList<SkillGroup> skillGroups = new ArrayList<SkillGroup>();

	/**
	 * 开始组
	 * 
	 * @param uri
	 * @param localName
	 * @param qName
	 * @param attributes
	 * @throws org.xml.sax.SAXException
	 */
	private void startGroup(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws org.xml.sax.SAXException
	{
		if ("group".equals(qName))
		{
			skillGroupID = XmlUtil.parseInt(attributes.getValue("gid"), 0);

			skillGroupData = new SkillGroupData();
			skillGroupData.type = XmlUtil.parseInt(attributes.getValue("type"), 0);
			skillGroupData.faction = XmlUtil.parseInt(attributes.getValue("faction"), 0);
			skillGroupData.factionLv = XmlUtil.parseInt(attributes.getValue("factionLvl"), 0);
			skillGroupData.name = XmlUtil.parseString(attributes.getValue("gname"), "");
			skillGroupData.desc = "";
			skillGroupData.hotKey = XmlUtil.parseString(attributes.getValue("key"), "");
			skillGroupData.skills = XmlUtil.parseString(attributes.getValue("skills"), "");
			skillGroupData.books = XmlUtil.parseString(attributes.getValue("activation"), "");
			skillGroupData.quality=XmlUtil.parseInt(attributes.getValue("quality"), 0);
		}
		else if ("groupData".equals(qName))
		{
			SkillGroupData groupData = new SkillGroupData();
			groupData.type = skillGroupData.type;
			groupData.faction = skillGroupData.faction;
			groupData.factionLv = skillGroupData.factionLv;
			groupData.name = skillGroupData.name;
			groupData.desc = XmlUtil.parseString(attributes.getValue("upgradeDesc"), skillGroupData.desc);
			groupData.hotKey = skillGroupData.hotKey;
			groupData.skills = skillGroupData.skills;
			groupData.books = skillGroupData.books;
			groupData.quality=skillGroupData.quality;

			skillGroupData.desc = groupData.desc;

			String nodeHash = groupData.toString();
			if (!skillGroupDataNodePool.containsKey(nodeHash))
			{
				skillGroupDataNodeID++;
				skillGroupDataNodePool.put(nodeHash, skillGroupDataNodeID);
			}

			SkillGroup skillGroup = new SkillGroup();
			skillGroup.id = skillGroupID;
			skillGroup.level = XmlUtil.parseInt(attributes.getValue("lvl"), 0);
			skillGroup.cooldown = XmlUtil.parseInt(attributes.getValue("cooldown"), 0);
			skillGroup.var1 = XmlUtil.parseString(attributes.getValue("var1"), "");
			skillGroup.var2 = XmlUtil.parseString(attributes.getValue("var2"), "");
			skillGroup.skillGrpuDataID = skillGroupDataNodePool.get(nodeHash);

			String[] skillIDs = groupData.skills.split(",");
			for (int i = 0; i < skillIDs.length; i++)
			{
				int skillID = XmlUtil.parseInt(skillIDs[i], 0);
				int skillLv = skillGroup.level;
				if (skillID != 0)
				{
					Skill skill = skillHash.get(skillID + "_" + skillLv);
					if (skill != null)
					{
						Integer[] buffIDs = skill.consumeBuffs.toArray(new Integer[skill.consumeBuffs.size()]);
						for (int buffID : buffIDs)
						{
							skillGroup.consumeBuff = buffID;
						}
					}
					else
					{
						System.out.println("技能组(id=" + skillGroup.id + ",level=" + skillGroup.level + ",name=" + skillGroupData.name + ") 的子技能未找到！ (id=" + skillID + ",level=" + skillLv + ")");
					}
				}
			}

			skillGroups.add(skillGroup);
		}
	}

	/**
	 * 结束组
	 * 
	 * @param uri
	 * @param localName
	 * @param qName
	 * @throws org.xml.sax.SAXException
	 */
	private void endGroup(String uri, String localName, String qName) throws org.xml.sax.SAXException
	{

	}

	/**
	 * 获取所有技能组数据
	 * 
	 * @return
	 */
	private String getAllSkillGroups()
	{
		StringBuilder sb = new StringBuilder();

		String[] datas = skillGroupDataNodePool.keySet().toArray(new String[skillGroupDataNodePool.size()]);
		Arrays.sort(datas, new Comparator<String>()
		{
			@Override
			public int compare(String o1, String o2)
			{
				int id1 = skillGroupDataNodePool.get(o1);
				int id2 = skillGroupDataNodePool.get(o2);
				if (id1 < id2)
				{
					return -1;
				}
				else if (id1 > id2)
				{
					return 1;
				}
				return 0;
			}
		});
		sb.append("\t<groupDatas>\n");
		for (String data : datas)
		{
			sb.append("\t\t<groupData id=\"" + skillGroupDataNodePool.get(data) + "\"" + data.substring(data.indexOf(" ")) + "\n");
		}
		sb.append("\t</groupDatas>\n");

		Collections.sort(skillGroups, new Comparator<SkillGroup>()
		{
			@Override
			public int compare(SkillGroup o1, SkillGroup o2)
			{
				if (o1.id < o2.id)
				{
					return -1;
				}
				else if (o1.id > o2.id)
				{
					return 1;
				}
				else
				{
					if (o1.level < o2.level)
					{
						return -1;
					}
					else if (o1.level > o2.level)
					{
						return 1;
					}
				}
				return 0;
			}
		});
		sb.append("\t<groups>\n");
		for (SkillGroup group : skillGroups)
		{
			sb.append(String.format("\t\t<group id=\"%s\" level=\"%s\" cd=\"%s\" consume=\"%s\" var1=\"%s\" var2=\"%s\" dataID=\"%s\"/>\n", group.id, group.level, group.cooldown, group.consumeBuff, group.var1, group.var2, group.skillGrpuDataID));
		}
		sb.append("\t</groups>\n");

		return sb.toString();
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------
	//
	// 标签重构
	//
	// -----------------------------------------------------------------------------------------------------------------------------------------

	private SkillLabel label;

	private ArrayList<SkillLabel> labels = new ArrayList<SkillLabel>();

	/**
	 * 开始标签
	 * 
	 * @param uri
	 * @param localName
	 * @param qName
	 * @param attributes
	 * @throws org.xml.sax.SAXException
	 */
	private void startLabel(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws org.xml.sax.SAXException
	{
		if ("label".equals(qName))
		{
			label = new SkillLabel();
			label.id = XmlUtil.parseInt(attributes.getValue("id"), 0);
			label.attireID = "";
			label.attireType = 0;
			label.icon = XmlUtil.parseInt(attributes.getValue("icon"), 0);
			label.desc = XmlUtil.parseString(attributes.getValue("desc"), "");
			label.time = XmlUtil.parseInt(attributes.getValue("time"), 0);
			label.count = XmlUtil.parseInt(attributes.getValue("count"), 0);
			label.visible = !XmlUtil.parseString(attributes.getValue("display"), "").equals("false");
			label.effect = XmlUtil.parseInt(attributes.getValue("effect"), 0);
		}
		else if ("buff".equals(qName))
		{
			label.buffs.add(XmlUtil.parseInt(attributes.getValue("id"), 0));
		}
		else if ("hitEffect".equals(qName))
		{
			label.attireID = XmlUtil.parseString(attributes.getValue("attireId"), "").replaceAll("^[\\d+_]+", "");
			label.attireType = XmlUtil.parseInt(attributes.getValue("type"), 0);
		}
	}

	/**
	 * 结束标签
	 * 
	 * @param uri
	 * @param localName
	 * @param qName
	 * @throws org.xml.sax.SAXException
	 */
	private void endLabel(String uri, String localName, String qName) throws org.xml.sax.SAXException
	{
		if ("label".equals(qName))
		{
			labels.add(label);
		}
	}

	private String getAllLabelXML()
	{
		StringBuilder sb = new StringBuilder();

		Collections.sort(labels, new Comparator<SkillLabel>()
		{
			@Override
			public int compare(SkillLabel o1, SkillLabel o2)
			{
				if (o1.id < o2.id)
				{
					return -1;
				}
				else if (o1.id > o2.id)
				{
					return 1;
				}
				return 0;
			}
		});

		sb.append("\t<labels>\n");
		for (SkillLabel label : labels)
		{
			sb.append(String.format("\t\t<label id=\"%s\" icon=\"%s\" desc=\"%s\" time=\"%s\" count=\"%s\" visible=\"%s\" effect=\"%s\" attireID=\"%s\" attireType=\"%s\" buffs=\"%s\"/>\n", label.id, label.icon, label.desc, label.time, label.count, label.visible, label.effect, label.attireID, label.attireType, AbsXmlNode.formatList(label.buffs)));
		}
		sb.append("\t</labels>\n");

		return sb.toString();
	}

	private String filterAttireName(String name)
	{
		if (name == null)
		{
			return "";
		}
		return name.replaceFirst("^[\\d+_]+", "");
	}
}
