package txu.report.mainapp.grpc;

import org.springframework.stereotype.Service;
import txu.ErpServiceGrpc;
import txu.GetProductReply;
import txu.GetProductRequest;

@Service
public class HrmGrpcService extends ErpServiceGrpc.ErpServiceImplBase {

    @Override
    public void getProduct(GetProductRequest request, io.grpc.stub.StreamObserver<GetProductReply> responseObserver) {
        super.getProduct(request, responseObserver);
    }

    public HrmGrpcService() {
        super();
    }

}
