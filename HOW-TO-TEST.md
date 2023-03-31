## How to run the retry examples

All example projects provide a HTTP API that we use through cURL.
                       
### Step 1: Startup 

First start the example service that you want to test. See the subject submodule
for any build specifics.

Then open another shell window so you have at least two. 

In any of the shells, check that the service is up and connected to the database:

    curl --verbose http://localhost:8090/api

### Step 2: Get Order Request Form

Print an order form template that we will use to create orders:

    curl http://localhost:8090/api/order/template > form.json

### Step 3: Submit Order Form

Create at least one purchase order:

```bash
curl http://localhost:8090/api/order -H "Content-Type:application/json" -X POST -d "@form.json"
```

(or use an inline form)

```bash
curl http://localhost:8090/api/order -i -X POST \
-H 'Content-Type: application/json' \
-d '{
    "billAddress": {
        "address1": "Street 1.1",
        "address2": "Street 1.2",
        "city": "City 1",
        "country": "Country 1",
        "postcode": "Code 1"
    },
    "customerId": -1,
    "deliveryAddress": {
        "address1": "Street 2.1",
        "address2": "Street 2.2",
        "city": "City 2",
        "country": "Country 2",
        "postcode": "Code 2"
    },
    "requestId": "bc3cba97-dee9-41b2-9110-2f5dfc2c5dae"
}'
```

### Step 4: Produce a Read/Write Conflict

Assuming now that there is an existing order with ID 1 with status `PLACED`. We will read that order and
change the status to something else concurrently. This is known as a read-write or unrepeatable-read
conflict which is prevented by serializable. As a result, there will be an exception and a rollback. 

When this happens, the retry mechanism will kick in an retry the failed transaction. It will succeed 
since the two transactions are no longer conflicting (one of them committed).

To observe this predictably we'll use two separate session with a controllable delay between the read 
and write operations. 

Overview of the SQL operations executed (what the service will run):

```bash
BEGIN; -- T1
SELECT * FROM purchase_order WHERE id=1; -- T1 
-- T1: Assert that status is `PLACED`
-- T1: Suspend for 15s  
BEGIN; -- T2
SELECT * FROM purchase_order WHERE id=1; -- T2
-- Assert that status is still `PLACED`
UPDATE purchase_order SET order_status='PAID' WHERE id=1; -- T2 
COMMIT; -- T2 (OK)
UPDATE purchase_order SET order_status='CONFIRMED' WHERE id=1; -- T1 (ERROR!)
ROLLBACK; -- T1
```

Prepare two separate shell windows so you can run the commands concurrently.

First check that the order with ID 1 exist and has status PLACED (or anything else other than CONFIRMED)

```bash
curl http://localhost:8090/api/order/1
```

Now lets run the first transaction (T1) where there is a simulated 15 sec delay before
the commit (you can increase/decrease the time):

```bash
curl http://localhost:8090/api/order/1?status=CONFIRMED\&delay=15000 -i -X PUT
```

In less than 15 sec and before T1 commits, run the second transaction (T2)
from another session which doesnt wait and succeeds with a commit:

```bash
curl http://localhost:8090/api/order/1?status=PAID -i -X PUT
```

At this point T1 has no other choice than to rollback and that will trigger a retry:

```bash
ERROR: restart transaction: TransactionRetryWithProtoRefreshError: WriteTooOldError: write for key /Table/109/1/12/0 at timestamp 1669990868.355588000,0 too old; wrote at 1669990868.778375000,3: "sql txn" meta={id=92409d02 key=/Table/109/1/12/0 pri=0.03022202 epo=0 ts=1669990868.778375000,3 min=1669990868.355588000,0 seq=0} lock=true stat=PENDING rts=1669990868.355588000,0 wto=false gul=1669990868.855588000,0
```

The retry mechanism will catch that SQL exception, back off for a few hundred millis and then retry 
until it eventually succeeds (1 attempt). The expected outcome is a `200 OK` returned to both client
sessions. The final order status must be CONFIRMED since client 1 request (T1) was retried 
and eventually committed, thereby overwriting T2.

```bash
curl http://localhost:8090/api/order/1
```

That's it!