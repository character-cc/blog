# blog
Một Web có chức năng cơ bản crud cho bài viết người dùng.
# Cách config để chạy 
Đầu tiên bạn cần khởi động nginx trước: Vào thư mục nginx rồi sử dụng lệnh : ./nginx.exe
![image](https://github.com/user-attachments/assets/d979388b-9197-49df-acb7-43d6cf42260e)
Tiếp đến là keycloak với câu lệnh: bin/kc.bat start-dev --proxy-headers xforwarded --proxy-trusted-addresses=127.0.0.1 --hostname=http://localhost/keycloak --http-enabled=true --http-port=9090
![image](https://github.com/user-attachments/assets/6dcdb54e-50b9-4db6-b412-f0b70ea7d435)
Chờ 1 lúc để keycloak khởi động sau đó bạn khởi động react với câu lệnh : npm start
![image](https://github.com/user-attachments/assets/51884039-b5e3-4a8b-9ec6-dd82ae289c73)
Bạn cũng cần cài Redis nữa Với đường link hướng dẫn của họ: https://redis.io/docs/latest/operate/oss_and_stack/install/install-redis/
Sau đó bạn lấy mật khẩu redis máy bạn rồi thay đổi password của redis trong file application.property của spring ![image](https://github.com/user-attachments/assets/5d3855c3-6f45-4cfd-ae30-c83e78735036)
Sau khi làm các bước trên thì chạy spring nếu mà có lỗi mà spring báo clientregistration thì cần chạy lại keycloak.
Truy cập đường link : http://localhost  . Sẽ ra trang home.

Có một số chức năng mình đang phát triển
# Nếu không muốn config đây là phần giới thiệu ý tưởng của mình
Mình sử dụng keycloak như 1 Authorization Server . Đại khái sẽ chịu trách nhiệm cho việc đăng nhập đăng ký của người dùng giống kiểu đăng nhập qua facebook, google.Flow như sau:
![image](https://github.com/user-attachments/assets/c4a5278e-ac47-4b7d-a308-5d68cfdba090)

Với flow này sẽ xác định người dùng qua session còn keycloak chỉ chịu trác nhiệm sign in sign up. Do sử dụng flow này lên mình cần nginx làm trung gian đợi ở cổng 80. Khi người dùng truy cập sẽ đi qua nó . Sau đó nginx sẽ quyết định xem đến react hay spring hay keycloak . Mình config với đường link :"/" thì đến react , "/api" đến spring , "keycloak" đến keycloak. Do sử dụng nginx là trung gian nên sẽ tránh được cors và sẽ gửi được cookie để xác định session người dùng.

Mình sử dụng redis do chúng được xây sẵn hàm để mình quản lý cache một cách đơn giản hơn chủ yếu để lưu bảng điểm các bài viết , vị trí user xem đến đâu hay việc upload ảnh , hay đường link để backend redirect. Với việc hiển thị các bài viết trong trang Home mình có ý tưởng đơn giản là tính điểm những bài viết được đăng gần nhất trong 1 tuần rồi lưu vào zset của redis (Mỗi 24h sẽ tính lại 1 lần) để có thể lấy những bài viết có điểm cao nhất nếu không làm vậy mỗi khi truy cập trang Home sẽ lại phải truy vấn tới database để tính điểm .
![image](https://github.com/user-attachments/assets/a6b03a3f-b891-462e-ba76-74ef493dec98) hàm này trong postService.

Khi người dùng truy cập trang Home lần đầu tiên mình mà chưa đăng nhập sẽ dùng redis để lấy theo thứ tự từ trên xuống dưới theo điểm của các bài viết . Mỗi lần gọi api mình sẽ lấy thêm 10 do đó cần lưu lại vị trí lấy để tránh việc lấy trùng lặp các bài mà người dùng đã xem . Mình dùng session để lưu vị trí người dùng xem tới đâu ![image](https://github.com/user-attachments/assets/c1515d7d-ce32-42cc-82e1-f9d276847957)
 Mặc dù chưa đăng nhập nhưng mình cho spring tạo phiên . Dựa vào redis khi truy cập lần 2 lần 3 mình sẽ biết người dùng xem đến đâu . Hàm xử lý cái này là firstTimeVisit trong PostService.

Với người dùng chưa đăng nhập khi đã click vào vài bài mình sẽ xem danh mục của mỗi bài viết đó và lưu lại trong redis với danh mục nào nếu có số bài viết được click lớn hơn 4 mình sẽ lưu vào một Set trong 1 class có sessionScope . Khi người dùng truy cập trang home mình sẽ lấy các bài viết có tag 2 danh mục mà 2 danh mục đó có số phần tử nhiều nhất trong bảng điểm các bài viết đã tính sẵn. xem hàm manyTimeVisit trong PostService. Hàm này cũng dùng để cho cả trường hợp người dùng đã đăng nhập hay chỉ chọn 1 danh mục duy nhất trên trang home. 
![image](https://github.com/user-attachments/assets/a9e2b2d0-89ad-47c2-bf06-1311bb93f9d1)

Do hàm manyTimeVisit cần user đã đăng nhập cần phải có các danh mục mà hứng thú đã chọn lên mình đã tạo 1 filter bắt buộc khi người dùng đăng nhập lần đầu sẽ phải trọn danh mục mà họ hứng tú. Filter tên FirstTimeLogin trong pack config . Luôn kiểm tra xem người dùng có danh mục hứng thú của họ chưa để buộc họ chọn khi truy cập bất cứ đường link nào 
![image](https://github.com/user-attachments/assets/4b7dabdd-124a-4d26-b758-16b0629fa5a0)
Các filter khác chủ yếu dùng redis để lưu lại url của front End để khi người dùng đăng nhập xong sẽ redirect trang mà người dùng đang xem. 
Ví dụ: ở react có 1 Nút button với đường link : http://localhost/api/oauth2/authorization/keycloak khi click vào sẽ gửi tới spring và spring sẽ tạo ra đường link có các tham số cần thiết rồi gửi lại trình duyệt người dùng một đường link có chứa redirect tới keycloak để đăng nhập , sau khi đăng nhập xong keycloak sẽ gửi lại code xác thực cho spring và spring sẽ tạo authentication .Như vậy sau khi tạo authentication xong spring cần biết đường link của frontEnd mà đã ấn button đăng nhập để redirect lại. Vấn đề này mình giải quyết như sau : Khi người dùng nhấn vào đường link đăng nhập thì react cũng gửi theo đường link hiện tại của nó tới spring và spring sẽ sử dụng ip của người dùng để lưu lại frontEndUrl và khi đăng nhập xong sẽ lấy ra để redirect lại. Mình cũng không rõ cách xử lý này đúng không với do chạy localhost lên có xem ip cũng không tác dụng . Theo mình tra thì nginx có luôn gửi header có thể lấy ip ban đầu được gửi đi là X-Forwarded-For.Do chỉ muốn nó làm proxy để tránh cors lên mình cũng không động đọc tài liệu liên quan tới nó nhiều nên không biết chính xác liệu cách trên có vấn đề gì khi chạy thực tế. Bạn có thể xem qua lớp CustomAuthenticationEntryPoint mình viết trong pack config.

Mình sử dụng redis để lưu lại những địa chỉ ảnh của một bài viết cần lưu. Vấn đề như sau: 
![image](https://github.com/user-attachments/assets/08f9cab9-5616-4dcd-b6e8-ecb02793b5cc)

Mình sử dụng TinyMce để có thể hỗ trợ người dùng tùy chỉnh bài viết của họ . TinyMce sẽ trả lại đoạn mã html để mình lưu vào database và khi cần sẽ dùng react giải mã. Vấn đề ở đây là khi người dùng vấn đang trong quá trình viết nội dung để tạo bài viết thì khi họ thêm 1 ảnh bất kì từ máy của họ thì TinyMce yêu cầu ảnh đó phải được lưu trên backend ngay lập tức . Như vậy là mặc dù nội dung chưa lưu nhưng ảnh đã được lưu. Khi người dùng ấn tạo bài viết TinyMce sẽ gửi 1 đoạn mã html kiểu :"<img src="http://localhost/api/images/image1.png Vậy mặc định khi chạy cả đoạn code html như thế này thì không vấn đề gì nhưng mình muốn lấy ảnh đầu tiên của bài viết để cho việc giới thiệu ở trang home . Do đó mình cần lưu những đường link ảnh của vào database . Vậy để lưu được vào database mình dùng redis với key xác định là phiên người dùng lưu lại đường link các ảnh của bài viết . Và khi họ ấn submit thì mình sẽ lấy các đường link ở ảnh trong redis để lưu cùng title , category, content vào trong database rồi xóa key giữ ảnh đó . Và cũng một phần để với kịch bản người dùng vào form tạo bài rồi add vài cái ảnh nhưng không submit nếu vậy thì sẽ có những ảnh dư thừa có trong thư mục. Vậy mình tạo ra SessionDestroyListener để khi thu hồi phiên sẽ nếu key giữ ảnh vẫn tồn tại thì sẽ truy cập và xóa từng ảnh của nó.

Đó là các chức năng cơ bản mà mình dùng redis để xử lý. Do lần đầu làm flow như vậy và vì không thể test được các trường hợp lên có thể sẽ có nhiều bug với nhiều trường hợp xử lý không hợp lý và code có hơi rối. Có những chức năng mình sẽ bổ sung sau và sẽ sửa lại logic những phần không hợp lý.

![image](https://github.com/user-attachments/assets/d73c9977-1e03-4c89-bfdc-95f980a87de2)
![image](https://github.com/user-attachments/assets/f4920e14-fad8-437b-9fbc-c0cf3dd65653)
![image](https://github.com/user-attachments/assets/725826a9-7d55-4184-a854-3dcd2af56fbb)

![image](https://github.com/user-attachments/assets/26e2e65d-54f5-4b63-8e49-a4ecb68875f2)












 


