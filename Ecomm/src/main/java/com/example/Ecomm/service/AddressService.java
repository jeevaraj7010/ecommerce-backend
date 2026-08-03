package com.example.Ecomm.service;

import com.example.Ecomm.entity.Address;
import com.example.Ecomm.entity.User;
import com.example.Ecomm.repository.AddressRepository;
import com.example.Ecomm.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public List<Address> getUserAddresses(String username) {
        return addressRepository.findByUserUsernameOrderByDefaultAddressDescCreatedAtDesc(username);
    }

    public Address getAddressById(Long id, String username) {
        return addressRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new RuntimeException("Address not found with ID: " + id));
    }

    @Transactional
    public Address createAddress(String username, Address address) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        long count = addressRepository.countByUserUsername(username);
        if (count >= 5) {
            throw new RuntimeException("You can save a maximum of 5 delivery addresses.");
        }

        validateAddressInput(address);

        address.setUser(user);

        // If this is the first address or default requested, set default
        if (count == 0 || address.isDefaultAddress()) {
            unsetDefaultAddresses(username);
            address.setDefaultAddress(true);
        }

        return addressRepository.save(address);
    }

    @Transactional
    public Address updateAddress(Long id, String username, Address updated) {
        Address existing = getAddressById(id, username);

        validateAddressInput(updated);

        existing.setAddressType(updated.getAddressType());
        existing.setAddressLabel(updated.getAddressLabel());
        existing.setFullName(updated.getFullName());
        existing.setPhone(updated.getPhone());
        existing.setAlternatePhone(updated.getAlternatePhone());
        existing.setHouseNo(updated.getHouseNo());
        existing.setStreet(updated.getStreet());
        existing.setLandmark(updated.getLandmark());
        existing.setDeliveryInstructions(updated.getDeliveryInstructions());
        existing.setCity(updated.getCity());
        existing.setDistrict(updated.getDistrict());
        existing.setState(updated.getState());
        existing.setPincode(updated.getPincode());
        if (updated.getLatitude() != null) existing.setLatitude(updated.getLatitude());
        if (updated.getLongitude() != null) existing.setLongitude(updated.getLongitude());

        if (updated.isDefaultAddress()) {
            unsetDefaultAddresses(username);
            existing.setDefaultAddress(true);
        }

        return addressRepository.save(existing);
    }

    @Transactional
    public Address setDefaultAddress(Long id, String username) {
        Address address = getAddressById(id, username);
        unsetDefaultAddresses(username);
        address.setDefaultAddress(true);
        return addressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(Long id, String username) {
        List<Address> addresses = getUserAddresses(username);
        if (addresses.size() <= 1) {
            throw new RuntimeException("At least one delivery address is required.");
        }

        Address addressToDelete = getAddressById(id, username);
        boolean wasDefault = addressToDelete.isDefaultAddress();

        addressRepository.delete(addressToDelete);

        // If deleted address was default, make the most recent remaining address default
        if (wasDefault) {
            List<Address> remaining = getUserAddresses(username);
            if (!remaining.isEmpty()) {
                Address first = remaining.get(0);
                first.setDefaultAddress(true);
                addressRepository.save(first);
            }
        }
    }

    private void unsetDefaultAddresses(String username) {
        List<Address> userAddresses = addressRepository.findByUserUsernameOrderByDefaultAddressDescCreatedAtDesc(username);
        for (Address addr : userAddresses) {
            if (addr.isDefaultAddress()) {
                addr.setDefaultAddress(false);
                addressRepository.save(addr);
            }
        }
    }

    private void validateAddressInput(Address address) {
        if (address.getPhone() != null && !address.getPhone().trim().isEmpty()) {
            String phone = address.getPhone().trim();
            if (!phone.matches("^[6-9]\\d{9}$")) {
                throw new RuntimeException("Invalid phone number. Must be 10 digits starting with 6, 7, 8, or 9");
            }
        }
        if (address.getPincode() != null && !address.getPincode().trim().isEmpty()) {
            String pincode = address.getPincode().trim();
            if (!pincode.matches("^\\d{6}$")) {
                throw new RuntimeException("Invalid pincode. Must be a 6-digit number");
            }
        }
    }
}
