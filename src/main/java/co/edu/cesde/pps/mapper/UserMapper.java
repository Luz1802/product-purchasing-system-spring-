package co.edu.cesde.pps.mapper;

import co.edu.cesde.pps.dto.UserDTO;
import co.edu.cesde.pps.model.User;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para conversión entre User (Entity) y UserDTO.
 *
 * Responsabilidades:
 * - Convertir Entity a DTO (toDTO)
 * - Convertir DTO a Entity (toEntity)
 * - Manejar null safety
 * - Extraer datos de relaciones (Role)
 * - Calcular campos agregados (addressesCount)
 * - NO exponer datos sensibles (passwordHash)
 */
public class UserMapper {

    /**
     * Convierte User Entity a UserDTO.
     *
     * @param user Entity a convertir
     * @return UserDTO o null si user es null
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());

        // Extraer nombre del rol (relación)
        if (user.getRole() != null) {
            dto.setRoleName(user.getRole().getName());
        }

        dto.setEmail(user.getEmail());
        // NO copiar passwordHash (seguridad)
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());

        // Campos calculados
        dto.setFullName(user.getFullName()); // Método helper de User

        // Campos agregados (null safety)
        if (user.getAddresses() != null) {
            dto.setAddressesCount(user.getAddresses().size());
        } else {
            dto.setAddressesCount(0);
        }

        return dto;
    }

    /**
     * Convierte UserDTO a User Entity.
     *
     * NOTA: No convierte Role ni Addresses, eso se maneja en el servicio.
     *
     * @param dto DTO a convertir
     * @return User Entity o null si dto es null
     */
    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setUserId(dto.getUserId());
        // Role se debe asignar en el servicio
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setStatus(dto.getStatus());
        user.setCreatedAt(dto.getCreatedAt());

        return user;
    }

    /**
     * Convierte lista de User Entities a lista de UserDTOs.
     *
     * @param users Lista de entities
     * @return Lista de DTOs o lista vacía si users es null
     */
    public List<UserDTO> toDTOList(List<User> users) {
        if (users == null) {
            return List.of();
        }

        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte lista de UserDTOs a lista de User Entities.
     *
     * @param dtos Lista de DTOs
     * @return Lista de entities o lista vacía si dtos es null
     */
    public List<User> toEntityList(List<UserDTO> dtos) {
        if (dtos == null) {
            return List.of();
        }

        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}