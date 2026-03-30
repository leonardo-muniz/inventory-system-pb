package com.leonardomuniz;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InventorySystemApplicationTests {

    @Test
    @DisplayName("Deve carregar o contexto da aplicação e executar o método main com sucesso")
    void mainMethodTest() {
        // Passamos o argumento para desativar o servidor web e evitar o erro de "Port 8080 in use"
        InventorySystemApplication.main(new String[]{"--spring.main.web-application-type=none"});
    }
}
