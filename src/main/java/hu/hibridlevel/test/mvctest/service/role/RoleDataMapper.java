package hu.hibridlevel.test.mvctest.service.role;

import hu.hibridlevel.test.mvctest.dto.RoleDto;
import hu.hibridlevel.test.mvctest.model.Role;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class RoleDataMapper {

       public RoleDto mapRoleDtoFromEntity(Role role) {
        RoleDto roleDto = new RoleDto();
        BeanUtils.copyProperties(role, roleDto);
        return roleDto;
    }

    public Set<RoleDto> mapRoleDtoSetFromEntitySet(Collection<Role> roleList) {
        Set<RoleDto> dtoList = new HashSet<>();

        for (Role role : roleList) {
            dtoList.add(mapRoleDtoFromEntity(role));
        }
        return dtoList;
    }

    public Role mapRoleEntityFromDto(RoleDto userDto) {
        Role user = new Role();
        BeanUtils.copyProperties(userDto, user);
        return user;
    }

}
