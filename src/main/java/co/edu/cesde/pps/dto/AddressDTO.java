package co.edu.cesde.pps.dto;

import co.edu.cesde.pps.enums.AddressType;
import java.util.Objects;

/**
 * DTO para transferencia de datos de Dirección.
 *
 * Se utiliza para:
 * - Agregar nueva dirección
 * - Actualizar dirección existente
 * - Mostrar direcciones en perfil
 * - Selección de dirección en checkout
 */
public class AddressDTO {

    private Long addressId;
    private Long userId;
    private AddressType type;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private Boolean isDefault;

    // Constructor vacío
    public AddressDTO() {
    }

    // Constructor completo
    public AddressDTO(Long addressId, Long userId, AddressType type, String line1,
                      String line2, String city, String state, String country,
                      String postalCode, Boolean isDefault) {
        this.addressId = addressId;
        this.userId = userId;
        this.type = type;
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
        this.isDefault = isDefault;
    }

    // Getters y Setters

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    // Método helper para obtener dirección completa formateada
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(line1);
        if (line2 != null && !line2.trim().isEmpty()) {
            sb.append(", ").append(line2);
        }
        sb.append(", ").append(city);
        sb.append(", ").append(state);
        sb.append(", ").append(country);
        sb.append(" ").append(postalCode);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressDTO that = (AddressDTO) o;
        return Objects.equals(addressId, that.addressId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressId);
    }

    @Override
    public String toString() {
        return "AddressDTO{" +
                "addressId=" + addressId +
                ", type=" + type +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}