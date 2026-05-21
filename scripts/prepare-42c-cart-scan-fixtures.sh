#!/usr/bin/env zsh

set -euo pipefail

command -v curl >/dev/null
command -v jq >/dev/null

cart_base_url="${CART_BASE_URL:-http://localhost:18080}"
user_base_url="${USER_BASE_URL:-http://localhost:18081}"
product_base_url="${PRODUCT_BASE_URL:-http://localhost:18082}"

scan_user=$(curl -fsS -H 'Content-Type: application/json' \
  -d '{"email":"scan.create.cart@example.com","firstName":"Scan","lastName":"CreateCart","vip":true}' \
  "$user_base_url/users")
create_cart_user_id=$(printf '%s' "$scan_user" | jq -r '.id')

add_item_product=$(curl -fsS -H 'Content-Type: application/json' \
  -d '{"name":"Scan Add Product","description":"Fixture for 42Crunch add item scan","price":49.99}' \
  "$product_base_url/products")
add_item_product_id=$(printf '%s' "$add_item_product" | jq -r '.id')

remove_item_product=$(curl -fsS -H 'Content-Type: application/json' \
  -d '{"name":"Scan Remove Product","description":"Fixture for 42Crunch remove item scan","price":19.99}' \
  "$product_base_url/products")
remove_item_product_id=$(printf '%s' "$remove_item_product" | jq -r '.id')

checkout_product=$(curl -fsS -H 'Content-Type: application/json' \
  -d '{"name":"Scan Checkout Product","description":"Fixture for 42Crunch checkout scan","price":79.99}' \
  "$product_base_url/products")
checkout_product_id=$(printf '%s' "$checkout_product" | jq -r '.id')

history_product_one=$(curl -fsS -H 'Content-Type: application/json' \
  -d '{"name":"Scan History Product 1","description":"Fixture for 42Crunch history scan","price":120.00}' \
  "$product_base_url/products")
history_product_one_id=$(printf '%s' "$history_product_one" | jq -r '.id')

history_product_two=$(curl -fsS -H 'Content-Type: application/json' \
  -d '{"name":"Scan History Product 2","description":"Fixture for 42Crunch history scan","price":35.00}' \
  "$product_base_url/products")
history_product_two_id=$(printf '%s' "$history_product_two" | jq -r '.id')

get_cart=$(curl -fsS -H 'Content-Type: application/json' \
  -d "{\"userId\":\"$create_cart_user_id\",\"cartType\":\"VIP\"}" \
  "$cart_base_url/carts")
get_cart_id=$(printf '%s' "$get_cart" | jq -r '.id')

add_item_cart=$(curl -fsS -H 'Content-Type: application/json' \
  -d "{\"userId\":\"$create_cart_user_id\",\"cartType\":\"VIP\"}" \
  "$cart_base_url/carts")
add_item_cart_id=$(printf '%s' "$add_item_cart" | jq -r '.id')

remove_item_cart=$(curl -fsS -H 'Content-Type: application/json' \
  -d "{\"userId\":\"$create_cart_user_id\",\"cartType\":\"VIP\"}" \
  "$cart_base_url/carts")
remove_item_cart_id=$(printf '%s' "$remove_item_cart" | jq -r '.id')
curl -fsS -H 'Content-Type: application/json' \
  -d "{\"productId\":\"$remove_item_product_id\",\"quantity\":2}" \
  "$cart_base_url/carts/$remove_item_cart_id/items" >/dev/null

checkout_cart=$(curl -fsS -H 'Content-Type: application/json' \
  -d "{\"userId\":\"$create_cart_user_id\",\"cartType\":\"VIP\"}" \
  "$cart_base_url/carts")
checkout_cart_id=$(printf '%s' "$checkout_cart" | jq -r '.id')
curl -fsS -H 'Content-Type: application/json' \
  -d "{\"productId\":\"$checkout_product_id\",\"quantity\":1}" \
  "$cart_base_url/carts/$checkout_cart_id/items" >/dev/null

delete_cart=$(curl -fsS -H 'Content-Type: application/json' \
  -d "{\"userId\":\"$create_cart_user_id\",\"cartType\":\"VIP\"}" \
  "$cart_base_url/carts")
delete_cart_id=$(printf '%s' "$delete_cart" | jq -r '.id')

history_cart=$(curl -fsS -H 'Content-Type: application/json' \
  -d "{\"userId\":\"$create_cart_user_id\",\"cartType\":\"VIP\"}" \
  "$cart_base_url/carts")
history_cart_id=$(printf '%s' "$history_cart" | jq -r '.id')
curl -fsS -H 'Content-Type: application/json' \
  -d "{\"productId\":\"$history_product_one_id\",\"quantity\":1}" \
  "$cart_base_url/carts/$history_cart_id/items" >/dev/null
curl -fsS -H 'Content-Type: application/json' \
  -d "{\"productId\":\"$history_product_two_id\",\"quantity\":1}" \
  "$cart_base_url/carts/$history_cart_id/items" >/dev/null
curl -fsS -X POST "$cart_base_url/carts/$history_cart_id/checkout" >/dev/null

cat <<EOF
export SCAN42C_HOST="$cart_base_url"
export SCAN42C_SECURITY_BEARERAUTH="demo-token"
export SCAN42C_CREATE_CART_USER_ID="$create_cart_user_id"
export SCAN42C_GET_CART_ID="$get_cart_id"
export SCAN42C_ADD_ITEM_CART_ID="$add_item_cart_id"
export SCAN42C_ADD_ITEM_PRODUCT_ID="$add_item_product_id"
export SCAN42C_REMOVE_ITEM_CART_ID="$remove_item_cart_id"
export SCAN42C_REMOVE_ITEM_PRODUCT_ID="$remove_item_product_id"
export SCAN42C_CHECKOUT_CART_ID="$checkout_cart_id"
export SCAN42C_DELETE_CART_ID="$delete_cart_id"
export SCAN42C_TOP_PRODUCTS_USER_ID="$create_cart_user_id"
EOF