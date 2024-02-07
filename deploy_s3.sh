npm run build
cd dist

## stage setup
aws s3 cp . s3://app-facilio-in --recursive --acl public-read
aws cloudfront create-invalidation --distribution-id 'E3QZMRBWZV5A08' --paths / /index.html

## production setup
## aws s3 cp . s3://app-facilio-ae --recursive --acl public-read
## aws cloudfront create-invalidation --distribution-id 'E39LXNGCXFOEIZ' --paths / /index.html

#########

echo "Build copied to S3 bucket..."
cd ..
