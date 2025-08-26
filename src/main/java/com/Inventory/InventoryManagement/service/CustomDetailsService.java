package com.Inventory.InventoryManagement.service;

import com.Inventory.InventoryManagement.entity.Employee;
import com.Inventory.InventoryManagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomDetailsService implements UserDetailsService{
    @Autowired
    private EmployeeRepository employeeRepository;
    @Override
    public UserDetails loadUserByUsername(String userId)
    {
        Long id=Long.parseLong(userId);
        Employee employee=employeeRepository.findById(id).orElseThrow(()-> new UsernameNotFoundException("Employee with " +
                "id "+ id+" does not exist"));
        return new org.springframework.security.core.userdetails.User(String.valueOf(employee.getId()),
                employee.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_"+employee.getRole())));
    }
}
