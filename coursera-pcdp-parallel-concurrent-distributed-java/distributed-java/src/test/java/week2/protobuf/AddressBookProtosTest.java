package week2.protobuf;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import week2.protobuf.AddressBookProtos.Person.PhoneNumber;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static week2.protobuf.AddressBookProtos.AddressBook;
import static week2.protobuf.AddressBookProtos.Person;

class AddressBookProtosTest {

    private final String filePath = "address_book";

    @AfterEach
    public void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get(filePath));
    }

    @Test
    public void givenGeneratedProtobufClass_whenCreateClass_thenShouldCreateJavaInstance() {
        //when
        var email = "j@test.com";
        var id = new Random(10).nextInt(100);
        var name = "Saurabh Program";
        var number = PhoneNumber.newBuilder()
                .setNumber("01234567890").build();
        var person =
                Person.newBuilder()
                        .setId(id)
                        .setName(name)
                        .setEmail(email)
                        .addPhones(number)
                        .build();
        //then
        assertEquals(person.getEmail(), email);
        assertEquals(person.getId(), id);
        assertEquals(person.getName(), name);
        assertEquals(person.getPhones(0), PhoneNumber.newBuilder()
                .setNumber("01234567890")
                .build());
    }


    @Test
    public void givenAddressBookWithOnePerson_whenSaveAsAFile_shouldLoadFromFileToJavaClass() throws IOException {
        //given
        var email = "j@test.com";
        var id = new Random(10).nextInt(100);
        var name = "Saurabh Program";

        var number = PhoneNumber.newBuilder()
                .setNumber("01234567890")
                .setType(Person.PhoneType.HOME)
                .build();

        var person =
                Person.newBuilder()
                        .setId(id)
                        .setName(name)
                        .setEmail(email)
                        .addPhones(number)
                        .build();

        //when
        var addressBook = AddressBook.newBuilder().addPeople(person).build();

        try (var fos = new FileOutputStream(filePath)) {
            addressBook.writeTo(fos);
        }

        //then
        try (var fis = new FileInputStream(filePath)) {
            var deserialized =
                    AddressBook.newBuilder()
                            .mergeFrom(fis)
                            .build();

            ListPeople.print(deserialized);

            var people = deserialized.getPeople(0);
            assertEquals(people.getEmail(), email);
            assertEquals(people.getId(), id);
            assertEquals(people.getName(), name);
            assertEquals(people.getPhones(0), number);
        }
    }

}