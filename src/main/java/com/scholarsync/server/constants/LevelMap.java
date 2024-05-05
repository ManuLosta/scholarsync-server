package com.scholarsync.server.constants;

import com.scholarsync.server.types.levelType;
import java.util.Map;

public class LevelMap {
  public static Map<Integer, levelType> levelMap =
      Map.ofEntries(
          Map.entry(0, levelType.Newbie),
          Map.entry(100, levelType.Learner),
          Map.entry(300, levelType.Initiate),
          Map.entry(600, levelType.Contender),
          Map.entry(1000, levelType.Skilled),
          Map.entry(1500, levelType.Veteran),
          Map.entry(2500, levelType.Master),
          Map.entry(3000, levelType.Grand_Master),
          Map.entry(4000, levelType.Champion),
          Map.entry(5000, levelType.Legend));
}
