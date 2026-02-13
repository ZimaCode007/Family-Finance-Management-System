package com.ffms.app.service;

import com.ffms.app.model.Family;
import com.ffms.app.model.User;
import com.ffms.app.model.enums.Role;
import com.ffms.app.repository.FamilyRepository;
import com.ffms.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;

    public Family getFamily(Long id) {
        return familyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Family not found"));
    }

    @Transactional
    public Family updateFamily(Long id, Family updated) {
        Family family = getFamily(id);
        family.setName(updated.getName());
        family.setDescription(updated.getDescription());
        return familyRepository.save(family);
    }

    public List<User> getMembers(Long familyId) {
        return userRepository.findByFamilyId(familyId);
    }

    @Transactional
    public void addMember(Long familyId, String username) {
        Family family = getFamily(familyId);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getFamily() != null) {
            throw new RuntimeException("User already belongs to a family");
        }

        user.setFamily(family);
        user.setRole(Role.MEMBER);
        userRepository.save(user);
    }

    @Transactional
    public void removeMember(Long familyId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getFamily() == null || !user.getFamily().getId().equals(familyId)) {
            throw new RuntimeException("User does not belong to this family");
        }

        user.setFamily(null);
        user.setRole(Role.MEMBER);
        userRepository.save(user);
    }
}
