package com.yas.customer.service;

import static com.yas.customer.util.SecurityContextUtils.setUpSecurityContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.AccessDeniedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.customer.model.UserAddress;
import com.yas.customer.repository.UserAddressRepository;
import com.yas.customer.viewmodel.address.ActiveAddressVm;
import com.yas.customer.viewmodel.address.AddressDetailVm;
import com.yas.customer.viewmodel.address.AddressPostVm;
import com.yas.customer.viewmodel.address.AddressVm;
import com.yas.customer.viewmodel.useraddress.UserAddressVm;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class UserAddressServiceTest {

  private UserAddressRepository userAddressRepository;
  private LocationService locationService;
  private UserAddressService userAddressService;

  private static final String USER_ID = "user123";

  @BeforeEach
  void setUp() {
    userAddressRepository = mock(UserAddressRepository.class);
    locationService = mock(LocationService.class);
    userAddressService = new UserAddressService(userAddressRepository, locationService);
  }

  @Test
  void testGetUserAddressList_isAuthenticated_returnActiveAddressList() {
    setUpSecurityContext(USER_ID);

    List<UserAddress> userAddresses = List.of(
        UserAddress.builder().id(1L).userId(USER_ID).addressId(1L).isActive(true).build(),
        UserAddress.builder().id(2L).userId(USER_ID).addressId(2L).isActive(false).build()
    );

    List<AddressDetailVm> addressDetails = List.of(
        new AddressDetailVm(1L, "John", "1234567890", "123 Main St", "City", "12345",
            1L, "District", 1L, "State", 1L, "Country"),
        new AddressDetailVm(2L, "Jane", "0987654321", "456 Oak Ave", "Town", "54321",
            2L, "District2", 2L, "State2", 2L, "Country2")
    );

    when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(userAddresses);
    when(locationService.getAddressesByIdList(any())).thenReturn(addressDetails);

    List<ActiveAddressVm> result = userAddressService.getUserAddressList();

    assertEquals(2, result.size());
    assertTrue(result.get(0).isActive());
    assertFalse(result.get(1).isActive());
  }

  @Test
  void testGetUserAddressList_isAnonymousUser_throwAccessDeniedException() {
    setUpSecurityContext("anonymousUser");

    AccessDeniedException thrown = assertThrows(AccessDeniedException.class,
        () -> userAddressService.getUserAddressList());

    assertTrue(thrown.getMessage().contains("LOGIN"));
  }

  @Test
  void testGetUserAddressList_isEmptyAddressList_returnEmptyList() {
    setUpSecurityContext(USER_ID);

    when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(Collections.emptyList());
    when(locationService.getAddressesByIdList(any())).thenReturn(Collections.emptyList());

    List<ActiveAddressVm> result = userAddressService.getUserAddressList();

    assertTrue(result.isEmpty());
  }

  @Test
  void testGetAddressDefault_isAuthenticated_returnAddressDetailVm() {
    setUpSecurityContext(USER_ID);

    UserAddress userAddress = UserAddress.builder()
        .id(1L).userId(USER_ID).addressId(1L).isActive(true).build();
    AddressDetailVm addressDetail = new AddressDetailVm(
        1L, "John", "1234567890", "123 Main St", "City", "12345",
        1L, "District", 1L, "State", 1L, "Country");

    when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID))
        .thenReturn(java.util.Optional.of(userAddress));
    when(locationService.getAddressById(1L)).thenReturn(addressDetail);

    AddressDetailVm result = userAddressService.getAddressDefault();

    assertEquals(addressDetail, result);
  }

  @Test
  void testGetAddressDefault_isAnonymousUser_throwAccessDeniedException() {
    setUpSecurityContext("anonymousUser");

    AccessDeniedException thrown = assertThrows(AccessDeniedException.class,
        () -> userAddressService.getAddressDefault());

    assertTrue(thrown.getMessage().contains("LOGIN"));
  }

  @Test
  void testGetAddressDefault_noDefaultAddress_throwNotFoundException() {
    setUpSecurityContext(USER_ID);

    when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID))
        .thenReturn(java.util.Optional.empty());

    NotFoundException thrown = assertThrows(NotFoundException.class,
        () -> userAddressService.getAddressDefault());

    assertTrue(thrown.getMessage().contains("User address not found"));
  }

  @Test
  void testCreateAddress_isFirstAddress_setActiveTrue() {
    setUpSecurityContext(USER_ID);

    AddressPostVm addressPostVm = new AddressPostVm(
        "John", "1234567890", "123 Main St", "City", "12345",
        1L, 1L, 1L);
    AddressVm addressVm = new AddressVm(1L, "John", "1234567890", "123 Main St",
        "City", "12345", 1L, 1L, 1L);

    when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(Collections.emptyList());
    when(locationService.createAddress(addressPostVm)).thenReturn(addressVm);
    when(userAddressRepository.save(any(UserAddress.class))).thenAnswer(i -> i.getArgument(0));

    UserAddressVm result = userAddressService.createAddress(addressPostVm);

    assertTrue(result.isActive());
    assertEquals(USER_ID, result.userId());
  }

  @Test
  void testCreateAddress_isNotFirstAddress_setActiveFalse() {
    setUpSecurityContext(USER_ID);

    AddressPostVm addressPostVm = new AddressPostVm(
        "Jane", "0987654321", "456 Oak Ave", "Town", "54321",
        2L, 2L, 2L);
    AddressVm addressVm = new AddressVm(2L, "Jane", "0987654321", "456 Oak Ave",
        "Town", "54321", 2L, 2L, 2L);

    List<UserAddress> existingAddresses = List.of(
        UserAddress.builder().id(1L).userId(USER_ID).addressId(1L).isActive(true).build()
    );

    when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(existingAddresses);
    when(locationService.createAddress(addressPostVm)).thenReturn(addressVm);
    when(userAddressRepository.save(any(UserAddress.class))).thenAnswer(i -> i.getArgument(0));

    UserAddressVm result = userAddressService.createAddress(addressPostVm);

    assertFalse(result.isActive());
  }

  @Test
  void testDeleteAddress_addressExists_deleteSuccessfully() {
    setUpSecurityContext(USER_ID);

    UserAddress userAddress = UserAddress.builder()
        .id(1L).userId(USER_ID).addressId(1L).isActive(true).build();

    when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, 1L)).thenReturn(userAddress);

    userAddressService.deleteAddress(1L);

    verify(userAddressRepository).delete(userAddress);
  }

  @Test
  void testDeleteAddress_addressNotFound_throwNotFoundException() {
    setUpSecurityContext(USER_ID);

    when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, 1L)).thenReturn(null);

    NotFoundException thrown = assertThrows(NotFoundException.class,
        () -> userAddressService.deleteAddress(1L));

    assertTrue(thrown.getMessage().contains("User address not found"));
    verify(userAddressRepository, never()).delete(any());
  }

  @Test
  void testChooseDefaultAddress_validId_setActiveTrueForTargetAddress() {
    setUpSecurityContext(USER_ID);

    List<UserAddress> userAddresses = List.of(
        UserAddress.builder().id(1L).userId(USER_ID).addressId(1L).isActive(true).build(),
        UserAddress.builder().id(2L).userId(USER_ID).addressId(2L).isActive(false).build(),
        UserAddress.builder().id(3L).userId(USER_ID).addressId(3L).isActive(false).build()
    );

    when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(userAddresses);

    userAddressService.chooseDefaultAddress(2L);

    ArgumentCaptor<List<UserAddress>> captor = ArgumentCaptor.forClass(List.class);
    verify(userAddressRepository).saveAll(captor.capture());

    List<UserAddress> savedAddresses = captor.getValue();
    assertFalse(savedAddresses.get(0).getIsActive());
    assertTrue(savedAddresses.get(1).getIsActive());
    assertFalse(savedAddresses.get(2).getIsActive());
  }
}
