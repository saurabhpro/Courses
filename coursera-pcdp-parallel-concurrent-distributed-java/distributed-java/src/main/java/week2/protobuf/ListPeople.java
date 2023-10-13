package week2.protobuf;

import week2.protobuf.AddressBookProtos.AddressBook;

import java.io.FileInputStream;

class ListPeople {

    // Iterates though all people in the AddressBook and prints info about them.
    static void print(AddressBook addressBook) {

        addressBook.getPeopleList().forEach(person -> {
            System.out.println("Person ID: " + person.getId());
            System.out.println("  Name: " + person.getName());
            if (person.hasEmail()) {
                System.out.println("  E-mail address: " + person.getEmail());
            }
            person.getPhonesList().forEach(phoneNumber -> {
                switch (phoneNumber.getType()) {
                    case MOBILE:
                        System.out.print("  Mobile phone #: ");
                        break;
                    case HOME:
                        System.out.print("  Home phone #: ");
                        break;
                    case WORK:
                        System.out.print("  Work phone #: ");
                        break;
                }
                System.out.println(phoneNumber.getNumber());
            });
        });
    }

    // Main function:  Reads the entire address book from a file and prints all
    //   the information inside.
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage:  ListPeople ADDRESS_BOOK_FILE");
            System.exit(-1);
        }

        // Read the existing address book.
        final var addressBook =
                AddressBook.parseFrom(new FileInputStream(args[0]));

        print(addressBook);
    }
}