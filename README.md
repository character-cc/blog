# blog
Một Web có chức năng cơ bản crud cho bài viết người dùng.
# Cách config để chạy 

Đầu tiên bạn cần khởi động nginx trước: Vào thư mục nginx rồi sử dụng lệnh : ./nginx.exe  
Đường link thư mục cần chạy : blog\nginx-1.26.2\nginx-1.26.2> ./nginx.exe

Như ảnh sau:
![image](https://github.com/user-attachments/assets/d979388b-9197-49df-acb7-43d6cf42260e)

Tiếp đến là vào thư mục keycloak với câu lệnh:

bin/kc.bat start-dev --proxy-headers xforwarded --proxy-trusted-addresses=127.0.0.1 --hostname=http://localhost/keycloak --http-enabled=true --http-port=9090
![image](https://github.com/user-attachments/assets/6dcdb54e-50b9-4db6-b412-f0b70ea7d435)
Chờ 1 lúc để keycloak khởi động.Sau đó bạn cài các dependencies của react bằng câu lệnh: npm install và tiếp đó khởi động react với câu lệnh : npm start
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

Mình sử dụng redis do chúng được xây sẵn vài kiểu cấu trúc . Do đó mình tận dụng nó để quản lý cache một cách đơn giản hơn như để lưu bảng điểm các bài viết , vị trí user xem đến đâu hay việc upload ảnh , hay đường link để backend redirect. Với việc hiển thị các bài viết trong trang Home mình có ý tưởng đơn giản là tính điểm những bài viết được đăng gần nhất trong 1 tuần rồi lưu vào zset của redis (Mỗi 24h sẽ tính lại 1 lần) để có thể lấy những bài viết có điểm cao nhất nếu không làm vậy mỗi khi truy cập trang Home sẽ lại phải truy vấn tới database để tính điểm .
![image](https://github.com/user-attachments/assets/a6b03a3f-b891-462e-ba76-74ef493dec98) hàm này trong postService.

Khi người dùng truy cập trang Home lần đầu tiên mà chưa đăng nhập, mình sẽ dùng redis để lấy theo thứ tự từ trên xuống dưới theo điểm của các bài viết . Mỗi lần gọi api mình sẽ lấy thêm 10 bài về cho react do đó cần lưu lại vị trí lấy để tránh việc lấy trùng lặp các bài mà người dùng đã xem . Mình dùng session để lưu vị trí người dùng xem tới đâu ![image](https://github.com/user-attachments/assets/c1515d7d-ce32-42cc-82e1-f9d276847957)
 Mặc dù chưa đăng nhập nhưng mình cho spring tạo phiên . Dựa vào redis khi truy cập lần 2 lần 3 mình sẽ biết người dùng xem đến đâu . Hàm xử lý cái này là firstTimeVisit trong PostService.

Với người dùng chưa đăng nhập khi đã click vào vài bài, mình sẽ xem danh mục của mỗi bài viết đó và lưu lại trong redis gắn với phiên của người dùng.Nếu danh mục nào có số click lớn hơn 4 thì mình sẽ lưu danh mục đó vào một Set nằm trong 1 class SessionUserNotAuth có sessionScope . Khi người dùng truy cập trang home, do có thể có các danh mục khó có thể được tag cùng trong bài viết, giả sử như 2 danh mục Spring và PHP sẽ gần như không có bài viết nào có danh mục như vậy. Do vậy mình sẽ tìm 2 danh mục trong số danh mục yêu thích của người dùng mà có nhiều bài viết nhất để lấy các bài viết hiển thị cho người dùng.Xem hàm manyTimeVisit trong PostService. Hàm này cũng dùng để cho cả trường hợp người dùng đã đăng nhập hay chưa đăng nhập mà có nhiều danh mục ưa thích và xử lý khi chỉ chọn 1 danh mục hứng thú trên trang home. 
![image](https://github.com/user-attachments/assets/a9e2b2d0-89ad-47c2-bf06-1311bb93f9d1)

Do hàm manyTimeVisit cần user đã đăng nhập cần phải có các danh mục mà họ hứng thú để phục vụ cho trang Home, lên mình đã tạo 1 filter khi người dùng đăng nhập lần đầu sẽ bắt buộc phải chọn danh mục mà họ hứng thú dù họ đang ở đường link nào của react. Filter tên FirstTimeLogin trong pack config.
![image](https://github.com/user-attachments/assets/4b7dabdd-124a-4d26-b758-16b0629fa5a0)

Các filter khác chủ yếu dùng redis để lưu lại url của front End để khi người dùng đăng nhập xong sẽ redirect trang mà người dùng vừa xem. 
Ví dụ: ở react có 1 Nút button với đường link : http://localhost/api/oauth2/authorization/keycloak khi click vào sẽ gửi tới spring và spring sẽ tạo ra đường link có các tham số cần thiết rồi gửi lại trình duyệt của người dùng một đường link có chứa redirect tới keycloak để đăng nhập , sau khi đăng nhập xong keycloak sẽ gửi lại code xác thực cho spring và spring sẽ tạo authentication .Như vậy sau khi tạo authentication xong spring cần biết đường link của frontEnd khi mà người dùng ấn button đăng nhập để redirect lại. Vấn đề này mình giải quyết như sau : Khi người dùng nhấn vào đường link đăng nhập thì react cũng gửi theo đường link hiện tại của nó tới spring và spring sẽ sử dụng ip của người dùng để lưu lại frontEndUrl và khi đăng nhập xong sẽ lấy ra để redirect lại.Tại sao không dùng sessionId để xác định thì do khi đăng nhập cái sessionId trước đăng nhập sẽ bị spring thay thế bằng sessionId mới. Mình cũng không rõ cách xử lý này đúng không với do chạy localhost lên có xem ip cũng không tác dụng . Theo mình tra thì nginx có luôn gửi header có thể lấy ip ban đầu được gửi đi là X-Forwarded-For.Do chỉ muốn nó làm proxy để tránh cors lên mình cũng không động đọc tài liệu liên quan tới nó nhiều nên không biết chính xác liệu cách trên có vấn đề gì khi chạy thực tế. Bạn có thể xem qua lớp CustomAuthenticationEntryPoint xử lý vấn để này trong pack config.

Mình sử dụng redis để lưu lại những địa chỉ ảnh của một bài viết cần lưu. Vấn đề như sau: 
![image](https://github.com/user-attachments/assets/08f9cab9-5616-4dcd-b6e8-ecb02793b5cc)

Mình sử dụng TinyMce để có thể hỗ trợ người dùng tùy chỉnh bài viết của họ như bôi đậm chữ,... TinyMce sẽ trả lại đoạn mã html để mình lưu vào database và khi cần sẽ dùng react giải mã. Vấn đề ở đây là khi người dùng vấn đang trong quá trình viết nội dung để tạo bài viết thì khi họ thêm 1 ảnh bất kì từ máy của họ thì TinyMce yêu cầu ảnh đó phải được lưu trên backend ngay lập tức(Lưu vào thư mục /images nằm trong spring, thư mục ngang hàng với src) . Như vậy là mặc dù nội dung chưa lưu nhưng ảnh đã được lưu. Khi người dùng ấn tạo bài viết TinyMce sẽ gửi 1 đoạn mã html kiểu :"<img src="http://localhost/api/images/image1.png cho backend lưu . Khi react yêu cầu thì backend ném cho react đoạn html này và react sẽ hiển thị form y như khi người dùng viết trên TinyMce. Nếu chỉ cần hiển thị thì không có vấn đề gì nhưng khi mình muốn lấy ảnh đầu tiên của bài viết thì buộc phải trích xuất đoạn html để lấy đường link ảnh trong src .Thay vì trích xuất mình dùng redis với key xác định là phiên người dùng lưu lại đường link các ảnh của bài viết khi mà TinyMce muốn lưu ảnh gọi tới api upload/image. Và khi người dùng ấn submit thì mình sẽ lấy các đường link ảnh trong redis để lưu cùng title , category, content vào trong database rồi xóa key giữ ảnh đó . Và cũng một phần để với kịch bản người dùng vào form tạo bài rồi add vài cái ảnh nhưng không submit nếu vậy thì sẽ có những ảnh dư thừa trong thư mục images. Vậy mình tạo ra SessionDestroyListener để khi thu hồi phiên  nếu key giữ ảnh vẫn tồn tại thì sẽ truy cập và xem các đường link để xóa ảnh trong thư mục images.

Đó là các chức năng cơ bản mà mình dùng redis để xử lý. Còn lại là crud các bài viết và tất nhiên cũng cập nhật số điểm của bài viết khi có tương tác xem hay like , comment bài viết. Do lần đầu làm flow như vậy và vì không thể test được các trường hợp lên có thể sẽ có nhiều bug với nhiều trường hợp xử lý không hợp lý. Có những chức năng mình sẽ bổ sung sau và sẽ sửa lại logic những phần không hợp lý nếu phát hiện ra.Đây là hình ảnh của frontend.

![image](https://github.com/user-attachments/assets/d73c9977-1e03-4c89-bfdc-95f980a87de2)
![image](https://github.com/user-attachments/assets/f4920e14-fad8-437b-9fbc-c0cf3dd65653)
![image](https://github.com/user-attachments/assets/725826a9-7d55-4184-a854-3dcd2af56fbb)
![image](https://github.com/user-attachments/assets/26e2e65d-54f5-4b63-8e49-a4ecb68875f2)
![image](https://github.com/user-attachments/assets/e0b53a20-e427-4eaa-8ae0-cace4f7b39df)













 


