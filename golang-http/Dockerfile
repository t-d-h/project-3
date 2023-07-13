FROM golang:1.20-alpine3.18 AS builder 
WORKDIR /go/src/build/app
COPY . .
RUN go build main.go


FROM alpine AS runner
WORKDIR /go/src/app
COPY --from=builder /go/src/build/app .
CMD ["main.go"]