import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Credopay MATM sample',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Credopay Sample'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  String error = "";
  static const platform = MethodChannel("new_activity");
  String txnType = "Balance Enquiry";
  bool isLoading = false;
  final TextEditingController _mobile = TextEditingController();
  final TextEditingController _amount = TextEditingController();

  Future<void> startPaymentActivity({changePassword}) async {
    setState(() {
      isLoading = true;
    });
    try {
      final result =
          await platform.invokeMethod('start_matm', <String, dynamic>{
        'transType': txnType,
        'amount': _amount.text.isEmpty || txnType == 'Balance Enquiry'
            ? 0
            : (int.parse(_amount.text) * 100),
        'change_password': changePassword != null && changePassword,
        'clientId': DateTime.now().millisecondsSinceEpoch.toString(),
        'mid': '2000037237',
        'mkey': 'a4hx^zneEr',
        'mobile': _mobile.text,
        'agentId': 'GIRT00009',
        'tid': 'E0090589',
      });

      if (result == 'CHANGE_PASSWORD') {
        startPaymentActivity(changePassword: true);
        setState(() {
          isLoading = false;
        });
      } else if (result == 'CHANGE_PASSWORD_SUCCESS') {
        setState(() {
          isLoading = false;
        });
        startPaymentActivity(changePassword: false);
      } else {
        setState(() {
          error = result.toString();
          isLoading = false;
        });
      }
      debugPrint('activity result: ${result.toString()}');
    } catch (e) {
      setState(() {
        isLoading = false;
        error = e.toString();
      });
      debugPrint('platform service error: ${e.toString()}');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(8.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              const Text(
                'Start MATM & MPOS Transactions',
              ),
              Text(
                error,
                style: Theme.of(context).textTheme.headline4,
              ),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: SizedBox(
                  height: 40.0,
                  child: ListView(
                    scrollDirection: Axis.horizontal,
                    children: [
                      ElevatedButton(
                        onPressed: () {
                          setState(() {
                            txnType = 'Balance Enquiry';
                          });
                        },
                        child: const Text('Balance Enquiry'),
                      ),
                      ElevatedButton(
                        onPressed: () {
                          setState(() {
                            txnType = 'Micro ATM';
                          });
                        },
                        child: const Text('Cash Withdrawal'),
                      ),
                      ElevatedButton(
                        onPressed: () {
                          setState(() {
                            txnType = 'MPOS';
                          });
                        },
                        child: const Text('Purchase'),
                      ),
                      ElevatedButton(
                        onPressed: () {
                          setState(() {
                            txnType = 'CASH_AT_POS';
                          });
                        },
                        child: const Text('Cash@Pos'),
                      ),
                      ElevatedButton(
                        onPressed: () {
                          setState(() {
                            txnType = 'UPI';
                          });
                        },
                        child: const Text('UPI'),
                      ),
                    ],
                  ),
                ),
              ),
              TextField(
                controller: _mobile,
                decoration: const InputDecoration(hintText: 'Mobile Number'),
              ),
              txnType != 'Balance Enquiry'
                  ? TextField(
                      controller: _amount,
                      decoration: const InputDecoration(hintText: 'Amount'),
                    )
                  : const SizedBox(),
              ElevatedButton(
                onPressed: startPaymentActivity,
                child: const Text('Proceed'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
