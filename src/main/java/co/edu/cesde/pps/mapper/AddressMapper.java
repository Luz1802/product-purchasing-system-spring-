package co.edu.cesde.pps.mapper;

import co.edu.cesde.pps.dto.AddressDTO;
import co.edu.cesde.pps.model.Address;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para conversión entre Address (Entity) y AddressDTO.
 *
 * Responsabilidades:
 * - Convertir Entity a DTO (toDTO)
 * - Convertir DTO a Entity (toEntity)
 * - Manejar null safety
 * - Extraer userId de la relación User
 */
public class AddressMapper {

    /**
     * Convierte Address Entity a AddressDTO.
     *
     * @param address Entity a convertir
     * @return AddressDTO o null si address es null
     */
    public AddressDTO toDTO(Address address) {
        if (address == null) {
            return null;
        }

        AddressDTO dto = new AddressDTO();
        dto.setAddressId(address.getAddressId());

        // Extraer userId de la relación
        if (address.getUser() != null) {
            dto.setUserId(address.getUser().getUserId());
        }

        dto.setType(address.getType());
        dto.setLine1(address.getLine1());
        dto.setLine2(address.getLine2());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setCountry(address.getCountry());
        dto.setPostalCode(address.getPostalCode());
        dto.setIsDefault(address.getIsDefault());

        return dto;
    }

    /**
     * Convierte AddressDTO a Address Entity.
     *
     * NOTA: No convierte User, eso se maneja en el servicio.
     *
     * @param dto DTO a convertir
     * @return Address Entity o null si dto es null
     */
    public Address toEntity(AddressDTO dto) {
        if (dto == null) {
            return null;
        }

        Address address = new Address();
        address.setAddressId(dto.getAddressId());
        // User se debe asignar en el servicio
        address.setType(dto.getType());
        address.setLine1(dto.getLine1());
        address.setLine2(dto.getLine2());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setPostalCode(dto.getPostalCode());
        address.setIsDefault(dto.getIsDefault());

        return address;
    }

    /**
     * Convierte lista de Address Entities a lista de AddressDTOs.
     *
     * @param addresses Lista de entities
     * @return Lista de DTOs o lista vacía si addresses es null
     */
    public List<AddressDTO> toDTOList(List<Address> addresses) {
        if (addresses == null) {
            return List.of();
        }

        return addresses.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte lista de AddressDTOs a lista de Address Entities.
     *
     * @param dtos Lista de DTOs
     * @return Lista de entities o lista vacía si dtos es null
     */
    public List<Address> toEntityList(List<AddressDTO> dtos) {
        if (dtos == null) {
            return List.of();
        }

        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}