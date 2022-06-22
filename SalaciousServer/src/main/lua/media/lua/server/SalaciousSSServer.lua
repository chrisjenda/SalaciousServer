--[[clientToserverCommand = {
    defaultName = 'clienttoserver',
}

if isClient() then
    return
end

clientToserverCommand.execute = function(player, args)

    local package = { args1 = args.args1, args2 = args.args2 }
    if args.args3 == nil then
        -- Do Stuff on Server
        -- Send a Command to the client with LUA as a callback if needed(Can also be done in Java of course)
        sendServerCommand(doctor, 'salacioustweaks', 'receiveBloodPressure', package)
    end
end

Events.OnClientCommand.Add(
    function(module, command, player, args)
        if module == 'salacioustweaks' and command == clientToserverCommand.defaultName then
            clientToserverCommand.execute(player, args)
        end
    end
)
--]]