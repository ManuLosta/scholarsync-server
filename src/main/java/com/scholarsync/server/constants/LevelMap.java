package com.scholarsync.server.constants;

import com.scholarsync.server.types.levelType;

import java.util.LinkedHashMap;
import java.util.Map;

public class LevelMap {
  public static Map<Integer, levelType> levelMap = new LinkedHashMap<>();

  static {
    levelMap.put(0, levelType.Newbie);
    levelMap.put(100, levelType.Learner);
    levelMap.put(300, levelType.Initiate);
    levelMap.put(600, levelType.Contender);
    levelMap.put(1000, levelType.Skilled);
    levelMap.put(1500, levelType.Veteran);
    levelMap.put(2500, levelType.Master);
    levelMap.put(3000, levelType.Grand_Master);
    levelMap.put(4000, levelType.Champion);
    levelMap.put(5000, levelType.Legend);
  }

  public static Integer getNextLevelNumber(levelType levelType) {
    boolean returnNext = false;
    for (Map.Entry<Integer, levelType> entry : levelMap.entrySet()) {
      if (returnNext) {
        return entry.getKey();
      }
      if (entry.getValue().equals(levelType)) {
        returnNext = true;
      }
    }
    return 0;
  }

  public static Integer getKeyFromLevel(levelType levelType) {
    for (Map.Entry<Integer, levelType> entry : levelMap.entrySet()) {
      if (entry.getValue().equals(levelType)) {
        return entry.getKey();
      }
    }
    return 0;
  }

  public static levelType getLevelByXp(Integer xp) {
    levelType currentLevel = levelType.Newbie;
    for (Map.Entry<Integer, levelType> entry : levelMap.entrySet()) {
      if (xp >= entry.getKey()) {
        currentLevel = entry.getValue();
      } else {
        break;
      }
    }
    return currentLevel;
  }
}
