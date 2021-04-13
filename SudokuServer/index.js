const express = require("express");
const router = express.Router();
const shell = require('child_process').spawn('cmd.exe')
const app = express();
const {exec} = require('child_process');
var cors = require('cors');
app.use(cors());
var cons = "";



app.use(express.json()); // support json encoded bodies
app.use(express.urlencoded({ extended: true })); // support encoded bodies

router.get('/start',(request,response) => {
    
    var num = request.query.cells;
    var threads= request.query.threads;
    
    cons = ""
    //var shell = require('child_process').spawn('cmd.exe')
    exec('cd C:\\Users\\Jeremy\\Documents\\Sudoku\\SudokuSolver\\out\\production\\SudokuSolver & java com.sudoku.Main '+num + ' ' + threads,(err, stdout, stderr) => {
        if (err) {
          // node couldn't execute the command
          console.log(err)
          return;
        }
      
        // the *entire* stdout and stderr (buffered)
        console.log(`stdout: ${stdout}`);
        console.log(`stderr: ${stderr}`);
      })

    
    //code to perform particular action.
    //To access GET variable use req.query() and req.params() methods.
    response.sendStatus(201);

});

router.get('/handle',(request,response) => {
    response.format({
        html: function () {
          response.send('<p>'+cons+'</p>');
        }});
});


router.get('/reset',(request,response) => {
    cons = "";
    //code to perform particular action.
    //To access GET variable use req.query() and req.params() methods.
    response.sendStatus(200);
});
router.post('/console',(request,response) => {
    //code to perform particular action.
    //To access GET variable use req.query() and req.params() methods.
    var str = request.header('msg');

    for (var i=0; i<str.length; i++) {
        if (str.charAt(i)!='*') {
            cons +=  str.charAt(i);
        } else {
            cons+='</br>';
        }
    }
    response.sendStatus(200);
});

// add router in the Express app.
app.use("/", router);

app.listen(3000,() => {
    console.log("Started on PORT 3000");
  })