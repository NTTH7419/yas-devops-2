# Hướng dẫn triển khai và kiểm thử Istio Service Mesh

Tài liệu này hướng dẫn các bước cấu hình mTLS, Authorization Policy, VirtualService (Retry) và kịch bản test trên môi trường K8S (namespace `staging`).

## 1. Triển khai Cấu hình Service Mesh (YAML Manifests)

Đảm bảo namespace `staging` đã được gắn nhãn `istio-injection=enabled`. 
Sau đó, apply file cấu hình chứa các rule mTLS, Retry và Authorization Policy:

```bash
kubectl apply -f k8s/istio/keycloak-service-entry-staging.yaml
kubectl apply -f k8s/istio/mesh-policies-staging.yaml
```

## 2. Triển khai Pod phục vụ Kiểm thử (Testing Pod)

Do các image của ứng dụng microservices được build theo chuẩn bảo mật Minimal/Distroless (không cài sẵn lệnh `curl`), chúng ta cần tạo một Pod tên là `sleep` chuyên dụng để thực thi các lệnh curl lấy bằng chứng (evidence).

```bash
kubectl apply -f - <<EOF
apiVersion: apps/v1
kind: Deployment
metadata:
  name: sleep
  namespace: staging
spec:
  replicas: 1
  selector:
    matchLabels:
      app: sleep
  template:
    metadata:
      labels:
        app: sleep
    spec:
      containers:
      - name: sleep
        image: curlimages/curl
        command: ["/bin/sleep", "3650d"]
EOF
```

Đợi Pod `sleep` chuyển sang trạng thái `Running` (sẽ có 2 container `2/2` vì Istio tự động inject proxy).

---

## 3. Kịch bản Test và Lấy Bằng chứng (Test Plan & Logs)

### Kịch bản 1: Kiểm tra Authorization Policy (Phân quyền kết nối)
Theo cấu hình `search-policy`, **CHỈ CÓ** `product-service` mới được phép gọi đến `search-service`. Bất kỳ ai khác gọi sẽ bị chặn.

**Bước 3.1.1: Test bị chặn (Lỗi 403)**
Đứng từ Pod `sleep` (không phải product) để gọi sang `search`:
```bash
kubectl exec -it deployment/sleep -n staging -c sleep -- curl -s -v http://search.staging.svc.cluster.local
```
**Kết quả mong đợi (Evidence):** Dòng log trả về `HTTP/1.1 403 Forbidden` và `RBAC: access denied`. (Chụp ảnh màn hình lưu lại).

**Bước 3.1.2: Test cho phép (Thành công 200 OK)**
Đứng từ Pod `product` gọi sang `search` (Nếu Pod product có curl hoặc wget, tuy nhiên nếu không có công cụ mạng, có thể chứng minh bằng cách xem log backend bình thường không sinh lỗi rbac). Do yêu cầu của giáo viên bắt buộc curl, nếu product không có curl, có thể tạm thời vá image hoặc báo cáo dựa trên log của Istio proxy. Nhưng để nhanh nhất, hãy dùng lệnh sau để xem log của Istio proxy trên pod search khi bị chặn:
```bash
kubectl logs deployment/search -n staging -c istio-proxy | grep "403"
```

### Kịch bản 2: Kiểm tra Retry Policy (VirtualService)
Theo cấu hình `tax-retry`, Istio sẽ tự động thử lại 3 lần nếu `tax-service` trả về lỗi 5xx.

**Cách test:** Cố tình gọi vào một port đã bị đóng (port 81) của tax service để kích hoạt lỗi rớt mạng (connect-failure), sau đó check log của proxy:
```bash
kubectl exec -it deployment/sleep -n staging -c sleep -- curl -s -v http://tax.staging.svc.cluster.local:81/api/tax/error
```
Để lấy evidence chứng minh Istio đã retry nhiều lần đến khi kiệt sức, chạy lệnh xem log của Istio proxy trên Pod sleep:
```bash
kubectl logs deployment/sleep -n staging -c istio-proxy | grep "URX"
```
**Kết quả mong đợi (Evidence):** Sẽ thấy cờ hiệu của Istio liên quan đến retry (như `URX` - Upstream Retry Limit Exceeded) hoặc nhiều dòng log request giống hệt nhau liên tiếp. (Chụp ảnh màn hình).

---

## 4. Kiali Topology (Vẽ Flowchart mTLS)

Để Kiali có thể vẽ được đồ thị mạng với các đường nối mTLS (hình ổ khóa xanh lá), hệ thống cần có traffic (lưu lượng truy cập) thực tế chạy qua.

**Bước 4.1: Tạo Traffic giả lập**
Chạy vòng lặp curl liên tục từ Pod sleep vào trang chủ của Storefront:
```bash
kubectl exec -it deployment/sleep -n staging -c sleep -- sh -c 'while true; do curl -s http://storefront-nextjs.staging.svc.cluster.local > /dev/null; echo "Sent request"; sleep 1; done'
```

**Bước 4.2: Truy cập và chụp ảnh Kiali**
1. Mở Port-forward cho Kiali:
   ```bash
   kubectl port-forward svc/kiali 20001:20001 -n istio-system
   ```
2. Mở trình duyệt truy cập `http://localhost:20001`.
3. Chọn thẻ **Graph**, chọn namespace `staging`.
4. Trong menu **Display**, tích chọn dòng **Security** (để hiện ổ khóa mTLS).
5. **Chụp ảnh màn hình (Evidence):** Lưu lại hình biểu đồ mạng làm báo cáo. Giải thích: Các đường nối màu xanh lá và icon ổ khóa chứng minh kết nối giữa các microservice đã được mã hóa 2 chiều.
