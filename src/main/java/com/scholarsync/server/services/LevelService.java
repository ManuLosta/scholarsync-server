package com.scholarsync.server.services;

import com.scholarsync.server.constants.LevelMap;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LevelService {
   @Autowired UserRepository userRepository;

   public void giveXp(User user, int amount) {
        user.setXp(user.getXp() + amount);
        user.setLevel(LevelMap.getLevelByXp(user.getXp() + amount));
        userRepository.save(user);
   }
}
