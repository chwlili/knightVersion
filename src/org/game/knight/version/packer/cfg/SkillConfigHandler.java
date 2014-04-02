package org.game.knight.version.packer.cfg;

import java.util.HashSet;
import java.util.Hashtable;

import org.xml.sax.helpers.DefaultHandler;

public class SkillConfigHandler extends DefaultHandler
{
	private boolean skillsing;
	private boolean grouping;
	private boolean labeling;

	private String currSkillID;
	private int currSkillState;
	private String currLabelID;

	private Hashtable<String, HashSet<String>> skillToActions = new Hashtable<String, HashSet<String>>();
	private Hashtable<String, HashSet<String>> skillToEffects = new Hashtable<String, HashSet<String>>();
	public Hashtable<String, HashSet<String>> groupToActions = new Hashtable<String, HashSet<String>>();
	public Hashtable<String, HashSet<String>> groupToEffects = new Hashtable<String, HashSet<String>>();
	public Hashtable<String, HashSet<String>> labelToEffects = new Hashtable<String, HashSet<String>>();

	public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws org.xml.sax.SAXException
	{
		if (skillsing)
		{
			if ("skill".equals(qName))
			{
				currSkillID = attributes.getValue("id");
			}
			else if ("attacker".equals(qName))
			{
				currSkillState = 1;
			}
			else if ("hurter".equals(qName))
			{
				currSkillState = 2;
			}
			else if ("motion".equals(qName) && currSkillState == 1)
			{
				HashSet<String> actions = skillToActions.get(currSkillID);
				if (actions == null)
				{
					actions = new HashSet<String>();
					skillToActions.put(currSkillID, actions);
				}
				actions.add(attributes.getValue("actionId"));
			}

			if ("hitEffect".equals(qName))
			{
				HashSet<String> effects = skillToEffects.get(currSkillID);
				if (effects == null)
				{
					effects = new HashSet<String>();
					skillToEffects.put(currSkillID, effects);
				}
				effects.add(filterAttireName(attributes.getValue("attireId")));
			}
		}
		else if (grouping)
		{
			if ("group".equals(qName))
			{
				String groupID = attributes.getValue("gid");
				String skillIDs = attributes.getValue("skills");

				HashSet<String> actions = groupToActions.get(groupID);
				if (actions == null)
				{
					actions = new HashSet<String>();
					groupToActions.put(groupID, actions);
				}

				HashSet<String> effects = groupToEffects.get(groupID);
				if (effects == null)
				{
					effects = new HashSet<String>();
					groupToEffects.put(groupID, effects);
				}

				String[] ids = skillIDs.split(",");
				for (String id : ids)
				{
					HashSet<String> actIDs = skillToActions.get(id);
					if (actIDs != null)
					{
						for (String actID : actIDs)
						{
							actions.add(actID);
						}
					}
					HashSet<String> effectIDs = skillToEffects.get(id);
					if (effectIDs != null)
					{
						for (String effectID : effectIDs)
						{
							effects.add(effectID);
						}
					}
				}
			}
		}
		else if (labeling)
		{
			if ("label".equals(qName))
			{
				currLabelID = attributes.getValue("id");
			}
			else if ("hitEffect".equals(qName))
			{
				HashSet<String> effects = labelToEffects.get(currLabelID);
				if (effects == null)
				{
					effects = new HashSet<String>();
					labelToEffects.put(currLabelID, effects);
				}

				effects.add(filterAttireName(attributes.getValue("attireId")));
			}
		}
		else
		{
			if ("skills".equals(qName))
			{
				skillsing = true;
			}
			else if ("groups".equals(qName))
			{
				grouping = true;
			}
			else if ("labels".equals(qName))
			{
				labeling = true;
			}
		}
	};

	public void endElement(String uri, String localName, String qName) throws org.xml.sax.SAXException
	{
		if (skillsing && "skills".equals(qName))
		{
			skillsing = false;
		}
		else if (grouping && "groups".equals(qName))
		{
			grouping = false;
		}
		else if (labeling && "labeling".equals(qName))
		{
			labeling = false;
		}
	};
	
	private String filterAttireName(String name)
	{
		if(name==null)
		{
			return "";
		}
		return name.replaceFirst("^[\\d+_]+", "");
	}
}