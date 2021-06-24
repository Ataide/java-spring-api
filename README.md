# JavaApi - Spring Framework

Projeto criado com Vscode, Extensão Java Essentials Pack, Marven for java, SpringBoot Initializr, Spring Boot Dashboard e outros....

## Docker Image

Para Criar a imagem docker digite `docker build -t java-api . `

Logo depois digite ` docker run -p 8080:8080 java-api `

Pronto. Se estiver tudo ok, o app estara rodando em ` http://localhost:8080 `

## Um pouco sobre o desenvolvimento

- Api criada com as seguintes features:

* Cadastro simples de Usuários. **(CRUD, "/users" - GET, PUT, POST, DELETE)
* Autenticação com JWT. **("/register" - POST | "/login" - POST)
* Rotas protegidas, (Usando um middleware mesmo.)* 
* Banco de Dados H2 (In Memory), Lembrando que caso a aplicação seja restartada os dados serão  perdidos.

- Fluxo de Desenvolvimento.

As rotas "/users" estão protegidas por token, para acessa-las basta cadastrar um usuario utilizando a rota "/register" que recebe 3 propriedades: nome, email, password.

Feito isso o usuário é cadastrado e poderá realizar o login em "/login" a api retorna com um token, caso os dados estejam corretos.

Pronto. Com esse token basta o app ou qualquer outro meio, enviar esse token por hearder Authorization, no formato Bearer. 

No registro tem o envio do token por cookie só pra exemplificar que pode ser usado também, porém a api só verifica o header.

Tive que realizar as configs de CORS. Normal... Logicamente isso deve ser revisto no caso de production.

Temos 2 Controllers, Um de authenticação e outro main, no de autenticação usei a abordagem de DTOs para respostas e injeção de response para retorno da api. No caso do Main onde é o Crud de Users usei  abordagem de retorno usando a classe ResponseEntity que é bem mais ao meu gosto.

A Secret do JWT foi feita mesmo HardCode.


### Model: 
Padrão Spring Boot - Utiliziando o Lombok cara Gerar os getters e setters. 

```
@Data
@Entity
public class User {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String name;
  
  @Column
  private String email;
  
  @Column
  private String password;
  
}

```

### Repósitorio:
Padrão Spring - Extendendo a Classe *JpaRepository implementando duas fuctions.

```
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(String email);

  User findByEmail(String email);
  
}

```

## Controllers

#### MainController - 

Padrão Spring - Usando como retorno a classe ResponseEntity do Spring bem interessante esse método *.orElse.

```

@RestController
public class MainController {
  
  @Autowired
  private UserRepository userRepository;  

  @RequestMapping("/")
  @ResponseStatus(HttpStatus.OK)
  public String checkApi() {
   return "Api ready";
  }
  
  @CrossOrigin
  @RequestMapping("/users")
  public List<User> list() {
    return userRepository.findAll();
  }

  @GetMapping("/users/{id}")
  public ResponseEntity<?> findOne(@PathVariable long id) {
    return userRepository.findById(id).map(result -> {
      return ResponseEntity.ok().body(result);
    }).orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/users")
  public ResponseEntity<?> edit(@RequestBody User userBody) {
     return userRepository.findById(userBody.getId()).map( result -> {
        if(userBody.getName() != null) {    
          result.setName(userBody.getName());
        }
        if(userBody.getEmail() != null) {
          result.setEmail(userBody.getEmail());          
        }
        if(userBody.getPassword() != null) {
          result.setPassword(userBody.getPassword());
        }         
        User updated = userRepository.save(result);

        return ResponseEntity.ok().body(updated);        
      }).orElse(ResponseEntity.notFound().build());
  }  

  @DeleteMapping("/users/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    return userRepository.findById(id).map(result -> {
      userRepository.deleteById(id);
      return ResponseEntity.ok().build();
    }).orElse(ResponseEntity.notFound().build());
  }

```

#### AuthController

``` 
@RestController
public class AuthController {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JwtProvider jwtProvider;

  @CrossOrigin
  @RequestMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  public UserResponseDto login(@RequestBody User userBody, HttpServletResponse response) throws IOException {
    try {
      User userExists = userRepository.findByEmail(userBody.getEmail());
     
      if(!userExists.getPassword().equals(userBody.getPassword())) {
        throw new Exception();        
      }
      String token = jwtProvider.createToken(userExists.getId().toString());   
      UserResponseDto userResponse = new UserResponseDto(userExists.getId(),userExists.getName(), token);
      return userResponse;
      
    } catch (Exception e) {      
      response.sendError(HttpStatus.BAD_REQUEST.value(), "wrong password/email"); 
      return null;
    }        
  }
  @CrossOrigin
  @RequestMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public void register(@RequestBody User user, HttpServletResponse response ) throws IOException {
    if(!userRepository.existsByEmail(user.getEmail())){
      userRepository.save(user);
      String token = jwtProvider.createToken(user.getId().toString());     
      Cookie cookie = new Cookie("token", token);   
      cookie.setPath("/");
      cookie.setMaxAge(60 * 30);
      response.addCookie(cookie);        
    } else {
      response.sendError(HttpStatus.BAD_REQUEST.value(), "email already in use");     
    }
  }  
  
}


```



