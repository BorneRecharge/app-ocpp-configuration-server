package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * JUnit test class for the {@link ChangeConfigurationRequest16}.
 */
class ChangeConfigurationRequest16Test {

  /**
   * Should not throw an exception when instantiating the record.
   */
  @Test
  public void correctConstructorShouldNotThrowException() {
    assertDoesNotThrow(() -> {
      new ChangeConfigurationRequest16("key", "value");
    });
  }

  /**
   * Should return the correct key.
   */
  @Test
  public void returnsCorrectKey() {
    var test = new ChangeConfigurationRequest16("key", "value");
    assertEquals("key", test.key());
  }

  /**
   * Should return the correct value.
   */
  @Test
  public void returnsCorrectValue() {
    var test = new ChangeConfigurationRequest16("key", "value");
    assertEquals("value", test.value());
  }

  /**
   * Should throw a {@link NullPointerException} if the key is null.
   */
  @Test
  public void throwsExceptionIfKeyIsNull() {
    assertThrows(NullPointerException.class, () -> new ChangeConfigurationRequest16(null, "value"));
  }

  /**
   * Should throw a {@link NullPointerException} if the value is null.
   */
  @Test
  public void throwsExceptionIfValueIsNull() {
    assertThrows(NullPointerException.class, () -> new ChangeConfigurationRequest16("key", null));
  }

  /**
   * Should throw an {@link IllegalArgumentException} if the key is more than 50 characters.
   */
  @Test
  public void throwsExceptionIfKeyIsIncorrect() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new ChangeConfigurationRequest16(
            "My key is more than 50 characters, which is pretty long !", "value"));
  }

  /**
   * Should throw an {@link IllegalArgumentException} if the value is more than 500 characters.
   */
  @Test
  public void throwsExceptionIfValueIsIncorrect() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new ChangeConfigurationRequest16(
            "key",
            """
                    Unfortunately, the value is more than 500 characters long.
                    Furthermore, it should be not accepted as a value according to
                    the OCPP 1.6 specification edited in 2019...
                    Please, restrain yourself from putting a long value into the packet
                    or the constructor would not be happy with you.
                    You really do not want to have the constructor angry at you and if it is the case,
                    well, farewell and good luck my friend, because you'll need luck to survive !
                    Oh no, the constructor is here... Please have mercy, NOOOOOOOOOOOOOOOOOOOOOO
            """));
  }
}
