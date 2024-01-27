package yummydelivery.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddressView {

    private long id;
    private String city;
    private String streetName;
    private String streetNumber;
    private String phoneNumber;
}
